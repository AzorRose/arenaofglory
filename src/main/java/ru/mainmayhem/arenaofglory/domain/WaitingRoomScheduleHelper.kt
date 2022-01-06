package ru.mainmayhem.arenaofglory.domain

import javax.inject.Inject
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.asCalendar
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.setCurrentDate
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

class WaitingRoomScheduleHelper @Inject constructor(
    @MatchJobInstance
    private val matchJob: PluginFiniteJob,
    private val settingsRepository: PluginSettingsRepository
) {

    /**
     * Открыта ли в данный момент комната ожидания
     */
    fun isWaitingRoomOpened(): Boolean {
        return matchJob.isActive() || preparingForMatch()
    }

    /**
     * Находимся ли мы в данный момент между датами открытия комнаты ожидания и началом матча
     */
    fun preparingForMatch(): Boolean {
        if (matchJob.isActive()) return false
        val openWaitingRoomBeforeMatchMillis =
            settingsRepository.getSettings().openWaitingRoomMins * Constants.MILLIS_IN_MINUTE
        val currDateMillis = System.currentTimeMillis()
        settingsRepository.getSettings().startArenaMatch.forEach { startArenaDate ->
            val startDateMillis = startArenaDate.asCalendar().setCurrentDate().timeInMillis
            val openWaitingRoomMillis = startDateMillis - openWaitingRoomBeforeMatchMillis
            if (currDateMillis in openWaitingRoomMillis..startDateMillis) {
                return true
            }
        }
        return false
    }

}