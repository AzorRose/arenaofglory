package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.*
import ru.mainmayhem.arenaofglory.data.hours
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.minutes
import java.util.*
import javax.inject.Inject

class MatchScheduleJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    settingsRepository: PluginSettingsRepository
) {

    private var job: Job? = null

    private val openWaitingRoom = settingsRepository.getSettings().openWaitingRoom
    private val startArenaMatch = settingsRepository.getSettings().startArenaMatch

    fun start(){
        if (job?.isActive == true)
            return
        job = coroutineScope.launch {
            try {
                val date = Date()
                while (isActive){
                    when{
                        date timeEqualsWith openWaitingRoom-> {
                            //todo
                        }
                        date timeEqualsWith startArenaMatch -> {
                            //todo
                        }
                        else -> delay(10_000)
                    }
                }
            } catch (t: Throwable){
                logger.error(
                    className = "MatchScheduleJob",
                    methodName = "job",
                    throwable = t
                )
            }
        }
    }

    fun stop(){
        job?.cancel(CancellationException())
        job = null
    }

    private infix fun Date.timeEqualsWith(date: Date): Boolean{
        val first = asCalendar()
        val second = date.asCalendar()
        return first.hours() == second.hours() && first.minutes() == second.minutes()
    }

    private fun Date.asCalendar(): Calendar{
        return Calendar.getInstance().apply {
            time = this@asCalendar
        }
    }

}