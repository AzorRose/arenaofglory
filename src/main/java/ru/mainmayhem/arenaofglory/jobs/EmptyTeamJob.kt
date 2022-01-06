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
import ru.mainmayhem.arenaofglory.domain.useCases.ArenaMatchEndedUseCase

private const val TIMER_STEP_SECONDS = 10L

class EmptyTeamJob @Inject constructor(
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob,
    logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchEndedUseCase: ArenaMatchEndedUseCase
): PluginCoroutineFiniteJob(
    coroutineScope = coroutineScope,
    dispatchers = dispatchers,
    logger = logger,
    timerStepTimeUnit = TimeUnit.SECONDS,
    timerStep = TIMER_STEP_SECONDS,
    duration = Constants.EMPTY_TEAM_DELAY_IN_SECONDS,
    durationTimeUnit = TimeUnit.SECONDS
) {

    override suspend fun onStart() {
        sendMessageToAllPlayersInMatch(
            "${org.bukkit.ChatColor.AQUA}Команда противника покинула битву"
        )
    }

    override suspend fun onCompletion() {
        matchJob.stop()
        arenaMatchEndedUseCase.handle(true)
    }

    override suspend fun onEach(leftTime: Long, timeUnit: TimeUnit) {
        sendMessageToAllPlayersInMatch(
            "До автоматической победы: $leftTime сек"
        )
    }

    private fun sendMessageToAllPlayersInMatch(message: String){
        arenaMatchMetaRepository.getPlayers().forEach { matchMember ->
            javaPlugin.server.getPlayer(matchMember.player.name)?.sendMessage(message)
        }
    }

}