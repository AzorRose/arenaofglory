package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToArenaEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToOutpostsEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToWaitingRoomEventHandler
import javax.inject.Inject

class PlayerTeleportEventInteractor @Inject constructor(
    private val tpToArenaEventHandler: TpToArenaEventHandler,
    private val tpToWaitingRoomEventHandler: TpToWaitingRoomEventHandler,
    private val tpToOutpostsEventHandler: TpToOutpostsEventHandler
) {

    fun handle(event: PlayerTeleportEvent){
        tpToWaitingRoomEventHandler.setNext(tpToOutpostsEventHandler)
        tpToArenaEventHandler.setNext(tpToWaitingRoomEventHandler)
        tpToArenaEventHandler.handle(event)
    }

}