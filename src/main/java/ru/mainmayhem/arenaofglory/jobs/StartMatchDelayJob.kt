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

/**
 * 10-ти секундная задержка на начало матча
 */
@Singleton
class StartMatchDelayJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    private val matchJob: MatchJob
) {

    //сколько времени осталось в секундах до истечения таймера
    var leftTime = 0
        private set

    private val timer = (0 until Constants.ARENA_START_MATCH_DELAY_IN_SECONDS)
        .asSequence()
        .asFlow()
        .onEach {
            leftTime = Constants.ARENA_START_MATCH_DELAY_IN_SECONDS - it
            sendMessageToAllPlayersInMatch(
                "До начала матча: $leftTime сек"
            )
            delay(1000)
        }
        .onCompletion {
            //todo открыть ворота
            matchJob.start()
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