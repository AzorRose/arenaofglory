package ru.mainmayhem.arenaofglory.jobs

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

abstract class PluginCoroutineFiniteJob(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val logger: PluginLogger,
    private val timerStepTimeUnit: TimeUnit,
    timerStep: Long,
    duration: Long,
    private val durationTimeUnit: TimeUnit
): PluginFiniteJob {

    private var job: Job? = null

    private val className = this::class.simpleName.orEmpty()

    private val durationMillis = durationTimeUnit.toMillis(duration)

    private val timerStepMillis = timerStepTimeUnit.toMillis(timerStep)

    @Volatile
    private var leftTimeMillis = durationMillis

    private val timer = flow<Int> {
        repeat((durationMillis / timerStepMillis).toInt()) { repetition ->
            leftTimeMillis = durationMillis - repetition * timerStepMillis
            onEach(
                leftTime = durationTimeUnit.convert(leftTimeMillis, TimeUnit.MILLISECONDS),
                timeUnit = durationTimeUnit
            )
            delay(timerStepMillis)
        }
        onCompletion()
    }.onStart {
        onStart()
    }

    @Synchronized
    override fun start() {
        if (isActive())
            return
        leftTimeMillis = durationMillis
        job = coroutineScope.launch(dispatchers.default) {
            try {
                timer.collect()
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    logger.error(
                        className = className,
                        methodName = "timer flow",
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
        leftTimeMillis = durationMillis
    }

    override fun getLeftTime(): Long = durationTimeUnit.convert(leftTimeMillis, TimeUnit.MILLISECONDS)

    override fun isActive(): Boolean = job?.isActive == true

    override fun getTimerStepTimeUnit(): TimeUnit = timerStepTimeUnit

    abstract suspend fun onEach(leftTime: Long, timeUnit: TimeUnit)

    abstract suspend fun onCompletion()

    open suspend fun onStart() { /*empty*/ }

}