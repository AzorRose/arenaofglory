package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
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
        val coordinates = Coordinates(to.x.toInt(), to.y.toInt(), to.z.toInt())
        outposts.forEach { (_, location) ->
            if (coordinatesComparator.compare(coordinates, location)) {
                event.isCancelled = true
                event.player.sendMessage("Вы не можете перемещаться на аванпосты с помощью консольных команд")
                return
            }
        }
        super.handle(event)
    }

}