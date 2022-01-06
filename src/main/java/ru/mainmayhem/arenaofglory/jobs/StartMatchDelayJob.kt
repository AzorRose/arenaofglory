package ru.mainmayhem.arenaofglory.jobs

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val JOB_DELAY_SECONDS = 1L

/**
 * 10-ти секундная задержка на начало матча
 */
class StartMatchDelayJob @Inject constructor(
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
    logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob
): PluginCoroutineFiniteJob(
    coroutineScope = coroutineScope,
    dispatchers = dispatchers,
    logger = logger,
    timerStepTimeUnit = TimeUnit.SECONDS,
    timerStep = JOB_DELAY_SECONDS,
    duration = Constants.ARENA_START_MATCH_DELAY_IN_SECONDS,
    durationTimeUnit = TimeUnit.SECONDS
) {

    override suspend fun onEach(leftTime: Long, timeUnit: TimeUnit) {
        sendMessageToAllPlayersInMatch(
            "${org.bukkit.ChatColor.GOLD}До начала матча: ${org.bukkit.ChatColor.YELLOW}$leftTime сек"
        )
    }

    override suspend fun onCompletion() {
        matchJob.start()
    }

    private fun sendMessageToAllPlayersInMatch(message: String) {
        arenaMatchMetaRepository.getPlayers().forEach { matchMember ->
            javaPlugin.server.getPlayer(matchMember.player.name)?.sendMessage(message)
        }
    }

}