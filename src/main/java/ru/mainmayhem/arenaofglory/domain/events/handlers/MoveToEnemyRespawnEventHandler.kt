package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.data.asCoordinates
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

class MoveToEnemyRespawnEventHandler @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val respawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val coordinatesComparator: CoordinatesComparator,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository
): BaseEventHandler<PlayerMoveEvent>() {

    override fun handle(event: PlayerMoveEvent) {

        if (!matchJob.isActive() || event.player.isNotInArena()) {
            super.handle(event)
            return
        }

        val coordinates = respawnCoordinatesRepository.getCachedCoordinates()
        val to = event.to
        if (to == null) {
            super.handle(event)
            return
        }
        val playerTargetCoordinates = to.asCoordinates()
        val playerId = event.player.uniqueId.toString()
        val playerFractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
        coordinates.forEach { (fractionId, respCoords) ->
            val coordinatesMatch = coordinatesComparator.compare(playerTargetCoordinates, respCoords)
            if (coordinatesMatch && playerFractionId != fractionId) {
                event.isCancelled = true
            }
        }
        super.handle(event)
    }

    private fun Player.isNotInArena(): Boolean {
        return arenaMatchMetaRepository.getPlayerById(uniqueId.toString()) == null
    }

}