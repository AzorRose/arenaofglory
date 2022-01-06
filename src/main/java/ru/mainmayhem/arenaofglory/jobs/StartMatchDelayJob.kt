package ru.mainmayhem.arenaofglory.jobs

import java.util.UUID
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
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
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

/**
 * 10-ти секундная задержка на начало матча
 */
@Singleton
class StartMatchDelayJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob
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
                "${org.bukkit.ChatColor.GOLD}До начала матча: ${org.bukkit.ChatColor.YELLOW}$leftTime сек"
            )
            delay(1000)
        }
        .onCompletion {
            matchJob.start()
        }

    private var job: Job? = null

    fun start(){
        if (job?.isActive == true)
            return
        job = coroutineScope.launch(dispatchers.default) {
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