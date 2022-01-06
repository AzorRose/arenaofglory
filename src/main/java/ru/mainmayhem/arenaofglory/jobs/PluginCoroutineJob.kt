package ru.mainmayhem.arenaofglory.jobs

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

abstract class PluginCoroutineJob(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val logger: PluginLogger,
    delayTimeUnit: TimeUnit,
    delay: Long
): PluginJob {

    private var job: Job? = null

    private val className = this::class.simpleName.orEmpty()

    private val delayInMills = delayTimeUnit.toMillis(delay)

    @Synchronized
    override fun start() {
        if (isActive())
            return
        job = coroutineScope.launch(dispatchers.default) {
            try {
                while (isActive) {
                    doRepeatedlyInBackground()
                    delay(delayInMills)
                }
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    logger.error(
                        className = className,
                        methodName = "doRepeatedlyInBackground",
                        throwable = t
                    )
                }
            }
        }
    }

    @Synchronized
    override fun stop() {
        job?.cancel(CancellationException())
        job = null
    }

    override fun isActive(): Boolean = job?.isActive == true

    abstract suspend fun doRepeatedlyInBackground()

}