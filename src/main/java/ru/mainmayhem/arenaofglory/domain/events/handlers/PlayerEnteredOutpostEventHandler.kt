package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.data.asCoordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder

class PlayerEnteredOutpostEventHandler @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val comparator: CoordinatesComparator,
    private val outpostsHolder: OutpostsHolder,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository
): BaseEventHandler<PlayerMoveEvent>() {

    override fun handle(event: PlayerMoveEvent) {
        val from = event.from.asCoordinates()
        val to = event.to?.asCoordinates()
        val playerId = event.player.uniqueId.toString()
        val player = arenaPlayersRepository.getCachedPlayerById(playerId)
        val inArena = arenaMatchMetaRepository.getPlayerById(playerId) != null
        if (to == null || player == null || inArena) {
            super.handle(event)
            return
        }
        outpostsRepository.getCachedOutposts().forEach { (outpost, location) ->
            if (!comparator.compare(from, location) && comparator.compare(to, location)) {
                outpostsHolder.addPlayer(player, outpost.id)
            }
        }
        super.handle(event)
    }

}