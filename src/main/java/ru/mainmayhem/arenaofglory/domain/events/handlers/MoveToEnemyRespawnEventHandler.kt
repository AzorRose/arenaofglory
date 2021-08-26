package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import javax.inject.Inject

class MoveToEnemyRespawnEventHandler @Inject constructor(
    private val logger: PluginLogger,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val respawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val coordinatesComparator: CoordinatesComparator
): BaseEventHandler<PlayerMoveEvent>() {

    override fun handle(event: PlayerMoveEvent) {
        val coordinates = respawnCoordinatesRepository.getCachedCoordinates()
        val to = event.to
        if (to == null){
            super.handle(event)
            return
        }
        val playerTargetCoordinates = Coordinates(to.x.toInt(), to.y.toInt(), to.z.toInt())
        val playerId = event.player.uniqueId.toString()
        val playerFractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
        coordinates.forEach { (fractionId, respCoords) ->
            val coordinatesMatch = coordinatesComparator.compare(playerTargetCoordinates, respCoords)
            if (coordinatesMatch && playerFractionId != fractionId){
                logger.info("Игрок ${event.player.getShortInfo()} пытается зайти на вражеский респавн")
                event.isCancelled = true
            }
        }
        super.handle(event)
    }

}