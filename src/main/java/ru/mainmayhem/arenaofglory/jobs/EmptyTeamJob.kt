package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.ArenaMatchEndedUseCase
import java.util.*
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmptyTeamJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val matchJob: MatchJob,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchEndedUseCase: ArenaMatchEndedUseCase
) {

    //сколько времени осталось в секундах до истечения таймера
    var leftTime = 0
        private set

    private val timer = flow<Int> {
        repeat(Constants.EMPTY_TEAM_DELAY_IN_SECONDS){
            leftTime = Constants.EMPTY_TEAM_DELAY_IN_SECONDS - it
            sendMessageToAllPlayersInMatch(
                "До автоматической победы: $leftTime сек"
            )
            delay(1000)
        }
        matchJob.stop()
        arenaMatchEndedUseCase.handle(true)
    }.onStart {
        sendMessageToAllPlayersInMatch(
            "Команда противника покинула матч"
        )
    }

    private var job: Job? = null

    fun start(){
        if (job?.isActive == true)
            return
        job = coroutineScope.launch {
            try {
                timer.collect()
            }catch (t: Throwable){
                if (t !is CancellationException){
                    logger.error(
                        className = "StartMatchDelayJob",
                        methodName = "timer flow",
                        throwable = t
                    )
                }
            }
        }
    }

    fun stop(){
        val wasActive = job?.isActive == true
        job?.cancel(CancellationException())
        job = null
        if (wasActive){
            sendMessageToAllPlayersInMatch(
                "Матч продолжается"
            )
        }
    }

    private fun sendMessageToAllPlayersInMatch(message: String){
        arenaMatchMetaRepository.getPlayers().forEach {
            javaPlugin.server.getPlayer(
                UUID.fromString(it.player.id)
            )?.sendMessage(message)
        }
    }

}