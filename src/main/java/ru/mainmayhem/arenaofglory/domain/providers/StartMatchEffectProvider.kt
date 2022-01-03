package ru.mainmayhem.arenaofglory.domain.providers

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.mainmayhem.arenaofglory.data.Constants.SECONDS_IN_MINUTE
import ru.mainmayhem.arenaofglory.data.Constants.TICKS_IN_SECOND
import ru.mainmayhem.arenaofglory.data.local.repositories.MatchResultsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

private const val EFFECT_LEVEL_DELTA = 10
private const val START_EFFECT_LEVEL = 5

class StartMatchEffectProvider @Inject constructor(
    private val matchResultsRepository: MatchResultsRepository,
    private val logger: PluginLogger,
    settingsRepository: PluginSettingsRepository
) {

    private val durationInTicks = settingsRepository.getSettings().matchDuration * SECONDS_IN_MINUTE * TICKS_IN_SECOND
    private val fractionBoostDefeats = settingsRepository.getSettings().fractionBoostDefeats

    fun provideEffect(fractionId: Long): PotionEffect? {
        val losesInRow = matchResultsRepository.getCachedResults()
            .takeLastWhile { it.looserFractionId == fractionId }
            .size
        logger.info("Фракция с id = $fractionId имеет $losesInRow проигрышей подряд")
        if (losesInRow < fractionBoostDefeats) return null
        val level = START_EFFECT_LEVEL + ((losesInRow - fractionBoostDefeats) * EFFECT_LEVEL_DELTA)
        logger.info("Уровень эффекта для фракции - $level")
        return PotionEffect(
            PotionEffectType.INCREASE_DAMAGE,
            durationInTicks,
            level
        )
    }

}