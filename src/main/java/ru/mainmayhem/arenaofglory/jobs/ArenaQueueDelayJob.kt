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
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.ArenaQueueDelayCompletedUseCase
import java.util.*
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArenaQueueDelayJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaQueueDelayCompletedUseCase: ArenaQueueDelayCompletedUseCase
) {

    private val millisInOneMinute = 60_000L

    //сколько времени осталось в минутах до истечения таймера
    var leftTime = 0
        private set

    private val timer = (0 until Constants.ARENA_QUEUE_DELAY_IN_MINUTES)
        .asSequence()
        .asFlow()
        .onEach {
            leftTime = Constants.ARENA_QUEUE_DELAY_IN_MINUTES - it
            sendMessageToAllPlayersInQueue(
                "До начала матча: $leftTime мин"
            )
            delay(millisInOneMinute)
        }
        .onCompletion {
            arenaQueueDelayCompletedUseCase.handle()
        }

    private var job: Job? = null

    fun start(){
        if (job?.isActive == true)
            return
        logger.info("Ожидание игроков: ${Constants.ARENA_QUEUE_DELAY_IN_MINUTES} мин")
        job = coroutineScope.launch {
            try {
                timer.collect()
            }catch (t: Throwable){
                if (t !is CancellationException){
                    logger.error(
                        className = "ArenaQueueDelayJob",
                        methodName = "timer flow",
                        throwable = t
                    )
                }
            }
        }
    }

    fun stop(){
        logger.info("Ожидание игроков остановлено")
        job?.cancel(CancellationException())
        job = null
    }

    private fun sendMessageToAllPlayersInQueue(message: String){
        val queue = arenaQueueRepository.get().values.toList()
        queue.forEach { fractionQueue ->
            fractionQueue.forEach {
                javaPlugin.server.getPlayer(
                    UUID.fromString(it.id)
                )?.sendMessage(message)
            }
        }
    }

}