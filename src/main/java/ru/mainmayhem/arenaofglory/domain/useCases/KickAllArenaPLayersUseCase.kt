package ru.mainmayhem.arenaofglory.domain.useCases

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.jobs.EmptyTeamJob
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import java.util.*
import javax.inject.Inject

/**
 * Телепортирует всех игроков из очереди и арены на спавн
 * Очищает очередь и данные о текущем матче
 */
class KickAllArenaPLayersUseCase @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val emptyTeamJob: EmptyTeamJob,
    private val matchJob: MatchJob,
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
        //fixme удаление игроков
        //arenaMatchMetaRepository.setPlayers(emptyList())
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