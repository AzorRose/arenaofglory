package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.asCoordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler

class TpToOutpostsEventHandler @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val coordinatesComparator: CoordinatesComparator
): BaseEventHandler<PlayerTeleportEvent>() {

    override fun handle(event: PlayerTeleportEvent) {
        val outposts = outpostsRepository.getCachedOutposts()
        val to = event.to
        if (to == null || outposts.isEmpty() || event.cause != PlayerTeleportEvent.TeleportCause.COMMAND) {
            super.handle(event)
            return
        }
        val coordinates = to.asCoordinates()
        outposts.forEach { (_, data) ->
            if (coordinatesComparator.compare(coordinates, data.calculatedLocation)) {
                event.isCancelled = true
                event.player.sendMessage("Вы не можете перемещаться на аванпосты с помощью консольных команд")
                return
            }
        }
        super.handle(event)
    }

}