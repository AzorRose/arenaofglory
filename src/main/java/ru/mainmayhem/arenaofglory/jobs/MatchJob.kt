package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.ArenaMatchEndedUseCase
import java.util.*
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchEndedUseCase: ArenaMatchEndedUseCase,
    private val fractionsRepository: FractionsRepository
) {

    private val millisInOneMinute = 60_000L

    private var job: Job? = null

    //сколько времени осталось в минутах до истечения таймера
    var leftTime = 0
        private set

    //активен таймер == активен матч
    val isActive
        get() = job?.isActive == true

    private val timer = (0 until Constants.MATCH_TIME_IN_MINUTES)
        .asSequence()
        .asFlow()
        .onEach {
            leftTime = Constants.MATCH_TIME_IN_MINUTES - it
            printCurrentResults()
            sendMessageToAllPlayersInMatch(
                "До конца матча: $leftTime мин"
            )
            delay(millisInOneMinute)
        }
        .onCompletion {
            arenaMatchEndedUseCase.handle()
        }

    fun start(){
        if (job?.isActive == true)
            return
        logger.info("Начало матча в ${Constants.MATCH_TIME_IN_MINUTES} мин")
        job = coroutineScope.launch {
            try {
                timer.collect()
            }catch (t: Throwable){
                if (t !is CancellationException){
                    logger.error(
                        className = "MatchJob",
                        methodName = "timer flow",
                        throwable = t
                    )
                }
            }
        }
    }

    fun stop(){
        job?.cancel(CancellationException())
        job = null
    }

    private fun sendMessageToAllPlayersInMatch(message: String){
        arenaMatchMetaRepository.getPlayers().forEach {
            javaPlugin.server.getPlayer(
                UUID.fromString(it.player.id)
            )?.sendMessage(message)
        }
    }

    private fun printCurrentResults(){
        val fractions = fractionsRepository.getCachedFractions()
        val message = StringBuilder()
        val scoreFontSettings = GOLD.toString() + BOLD.toString()
        val resultsFontSettings = YELLOW.toString()
        val evenFractionFontSettings = LIGHT_PURPLE.toString()
        val oddFractionFontSettings = AQUA.toString()
        message.append("${resultsFontSettings}Результаты:\n")
        arenaMatchMetaRepository.getFractionsPoints()
            .map { Pair(it.key, it.value) }
            .forEachIndexed { index, res ->
                val font = if (index == 0 || index % 2 == 0) evenFractionFontSettings else oddFractionFontSettings
                val fractionName = font + fractions.find { it.id ==  res.first}?.name
                message.append("$fractionName: $scoreFontSettings${res.second}\n")
            }
        sendMessageToAllPlayersInMatch(message.toString())
    }

}