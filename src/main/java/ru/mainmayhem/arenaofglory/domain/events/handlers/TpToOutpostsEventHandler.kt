package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import javax.inject.Inject

class TpToOutpostsEventHandler @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val coordinatesComparator: CoordinatesComparator
): BaseEventHandler<PlayerTeleportEvent>() {

    override fun handle(event: PlayerTeleportEvent) {
        val outposts = outpostsRepository.getCachedOutposts()
        val to = event.to
        if (to == null || outposts.isEmpty() || event.cause != PlayerTeleportEvent.TeleportCause.COMMAND){
            super.handle(event)
            return
        }
        val coordinates = Coordinates(to.x.toInt(), to.y.toInt(), to.z.toInt())
        outposts.forEach {
            if (coordinatesComparator.compare(coordinates, it.second)){
                event.isCancelled = true
                event.player.sendMessage("Вы не можете перемещаться на аванпосты с помощью консольных команд")
                return
            }
        }
        super.handle(event)
    }

}