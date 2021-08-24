package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin
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
            sendMessageToAllPlayersInMatch(
                "До конца матча: $leftTime мин"
            )
            delay(millisInOneMinute)
        }
        .onCompletion {
            logger.info("Матч закончен")
            //todo
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

}