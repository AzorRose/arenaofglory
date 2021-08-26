package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
class EmptyTeamJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val matchJob: MatchJob,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin
) {

    //сколько времени осталось в секундах до истечения таймера
    var leftTime = 0
        private set

    private val timer = (0 until Constants.EMPTY_TEAM_DELAY_IN_SECONDS)
        .asSequence()
        .asFlow()
        .onStart {
            sendMessageToAllPlayersInMatch(
                "Команда противника покинула матч"
            )
        }
        .onEach {
            leftTime = Constants.EMPTY_TEAM_DELAY_IN_SECONDS - it
            sendMessageToAllPlayersInMatch(
                "До автоматической победы: $leftTime сек"
            )
            delay(10000)
        }
        .onCompletion {
            matchJob.stop()
            //todo награда
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