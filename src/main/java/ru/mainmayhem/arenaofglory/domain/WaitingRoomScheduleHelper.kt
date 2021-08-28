package ru.mainmayhem.arenaofglory.domain

import ru.mainmayhem.arenaofglory.data.asCalendar
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.setCurrentDate
import ru.mainmayhem.arenaofglory.data.timeEqualsWith
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import java.util.*
import javax.inject.Inject

class WaitingRoomScheduleHelper @Inject constructor(
    private val matchJob: MatchJob,
    private val settingsRepository: PluginSettingsRepository
) {

    /**
     * Открыта ли в данный момент комната ожидания
     */
    fun isWaitingRoomOpened(): Boolean{
        return matchJob.isActive || preparingForMatch()
    }

    /**
     * Находимся ли мы в данный момент между датами открытия комнаты ожидания и началом матча
     */
    fun preparingForMatch(): Boolean {
        val currentDate = Date().asCalendar()
        val openWaitingRoomDate = settingsRepository.getSettings().openWaitingRoom.asCalendar().setCurrentDate()
        val matchStartDate = settingsRepository.getSettings().startArenaMatch.asCalendar().setCurrentDate()
        return (currentDate.time timeEqualsWith openWaitingRoomDate.time
                || currentDate.time timeEqualsWith matchStartDate.time
                || (currentDate.after(openWaitingRoomDate) && currentDate.before(matchStartDate)))
    }

}