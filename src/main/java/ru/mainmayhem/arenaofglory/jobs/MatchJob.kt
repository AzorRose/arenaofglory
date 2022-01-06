package ru.mainmayhem.arenaofglory.jobs

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import net.md_5.bungee.api.ChatColor.AQUA
import net.md_5.bungee.api.ChatColor.BOLD
import net.md_5.bungee.api.ChatColor.GOLD
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import net.md_5.bungee.api.ChatColor.YELLOW
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.ArenaMatchEndedUseCase

private const val TIMER_DELAY_MINUTES = 1L

class MatchJob @Inject constructor(
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
    logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchEndedUseCase: ArenaMatchEndedUseCase,
    private val fractionsRepository: FractionsRepository,
    settingsRepository: PluginSettingsRepository
): PluginCoroutineFiniteJob(
    coroutineScope = coroutineScope,
    dispatchers = dispatchers,
    logger = logger,
    timerStepTimeUnit = TimeUnit.MINUTES,
    timerStep = TIMER_DELAY_MINUTES,
    duration = settingsRepository.getSettings().matchDuration.toLong(),
    durationTimeUnit = TimeUnit.MINUTES
) {

    override suspend fun onCompletion() {
        arenaMatchEndedUseCase.handle(false)
    }

    override suspend fun onEach(leftTime: Long, timeUnit: TimeUnit) {
        printCurrentResults()
        sendMessageToAllPlayersInMatch(
            "До конца матча: $leftTime мин"
        )
    }

    private fun sendMessageToAllPlayersInMatch(message: String) {
        arenaMatchMetaRepository.getPlayers().forEach { matchMember ->
            javaPlugin.server.getPlayer(matchMember.player.name)?.sendMessage(message)
        }
    }

    private fun printCurrentResults() {
        val fractions = fractionsRepository.getCachedFractions()
        val message = StringBuilder()
        val scoreFontSettings = GOLD.toString() + BOLD.toString()
        val resultsFontSettings = YELLOW.toString()
        val evenFractionFontSettings = LIGHT_PURPLE.toString()
        val oddFractionFontSettings = AQUA.toString()
        message.append("${resultsFontSettings}Результаты:\n")
        arenaMatchMetaRepository.getFractionsPoints()
            .map { Pair(it.key, it.value) }
            .forEachIndexed { index, res ->
                val font = if (index == 0 || index % 2 == 0) evenFractionFontSettings else oddFractionFontSettings
                val fractionName = font + fractions.find { it.id == res.first }?.name
                message.append("$fractionName: $scoreFontSettings${res.second}\n")
            }
        sendMessageToAllPlayersInMatch(message.toString())
    }

}