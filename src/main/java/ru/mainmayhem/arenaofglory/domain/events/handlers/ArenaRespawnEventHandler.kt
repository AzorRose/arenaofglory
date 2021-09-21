package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.Location
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import javax.inject.Inject

/**
 * Если игрок участник арены, респавним его на территории его фракции
 */
class ArenaRespawnEventHandler @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin
): BaseEventHandler<PlayerRespawnEvent>() {

    override fun handle(event: PlayerRespawnEvent) {
        val playerId = event.player.uniqueId.toString()
        val player = arenaMatchMetaRepository.getPlayers().find { it.player.id == playerId }
        if (player != null){
            val coordinate = getRandomCoordinate(player.player.fractionId)
            if (coordinate == null){
                logger.error(
                    className = "ArenaRespawnEventHandler",
                    methodName = "handle",
                    throwable = NullPointerException("Не найден респавн для игрока ${event.player.getShortInfo()}")
                )
            } else {
                event.respawnLocation = Location(
                    javaPlugin.server.getWorld(Constants.WORLD_NAME),
                    coordinate.x.toDouble(),
                    coordinate.y.toDouble(),
                    coordinate.z.toDouble()
                )
            }
        }
        super.handle(event)
    }

    private fun getRandomCoordinate(fractionId: Long): Coordinates?{
        val respawn = arenaRespawnCoordinatesRepository.getCachedCoordinates()[fractionId] ?: return null
        return respawn.coordinates.random()
    }

}