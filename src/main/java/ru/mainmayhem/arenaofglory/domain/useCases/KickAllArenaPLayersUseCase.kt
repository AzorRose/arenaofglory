package ru.mainmayhem.arenaofglory.domain.useCases

import java.util.UUID
import javax.inject.Inject
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.dagger.annotations.EmptyTeamJobInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

/**
 * Телепортирует всех игроков из очереди и арены на спавн
 * Очищает очередь и данные о текущем матче
 */
class KickAllArenaPLayersUseCase @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    @EmptyTeamJobInstance
    private val emptyTeamJob: PluginFiniteJob,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob,
    private val javaPlugin: JavaPlugin
) {

    fun doKickPlayers(){
        arenaQueueRepository.getAll().forEach {
            it.kick()
        }
        arenaQueueRepository.clear()
        arenaMatchMetaRepository.getPlayers().forEach {
            it.player.kick()
        }
        arenaMatchMetaRepository.clear()
        matchJob.stop()
        emptyTeamJob.stop()
    }

    private fun ArenaPlayer.kick(){
        javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
            javaPlugin.server.getPlayer(
                UUID.fromString(id)
            )?.teleport(it.spawnLocation)
        }
    }

}