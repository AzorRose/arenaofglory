package ru.mainmayhem.arenaofglory.domain.providers

import ru.mainmayhem.arenaofglory.data.asCalendar
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.setCurrentDate
import ru.mainmayhem.arenaofglory.data.timeEqualsWith
import java.util.*
import javax.inject.Inject

/**
 * Отдаст либо текущее время, либо время ближайшего матча
 * Например, если матчи начинаютя в 8:00 и 12:00, то:
 * Если сейчас 8:00, то отдаст 8:00
 * Если 8:01, то отдаст 12:00
 */
class ClosestMatchDateProvider @Inject constructor(
    settingsRepository: PluginSettingsRepository,
    private val logger: PluginLogger
) {

    private val matchTimes = settingsRepository.getSettings().startArenaMatch.sortedBy { date ->
        date.time
    }

    fun provide(): Date {
        val currDate = Date()
        val closestMatchToday = matchTimes
            .find { date ->
                //отдаст  null, если сегодня уже не будет матчей
                currDate.timeEqualsWith(date) || currDate.before(date.asCalendar().setCurrentDate().time)
            }
        logger.info("closestMatchToday = $closestMatchToday")
        logger.info("matchTimes = $matchTimes")
        return closestMatchToday ?: matchTimes.first()
    }

}