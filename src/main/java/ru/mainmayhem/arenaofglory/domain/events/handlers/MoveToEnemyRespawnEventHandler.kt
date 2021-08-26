package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import javax.inject.Inject

class MoveToEnemyRespawnEventHandler @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val respawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val coordinatesComparator: CoordinatesComparator,
    private val matchJob: MatchJob,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository
): BaseEventHandler<PlayerMoveEvent>() {

    override fun handle(event: PlayerMoveEvent) {

        if (!matchJob.isActive || event.player.isNotInArena()){
            super.handle(event)
            return
        }

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
                event.isCancelled = true
            }
        }
        super.handle(event)
    }

    private fun Player.isNotInArena(): Boolean{
        return arenaMatchMetaRepository.getPlayers().find {
            it.player.id == uniqueId.toString()
        } == null
    }

}