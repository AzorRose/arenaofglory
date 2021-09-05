package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToArenaEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToWaitingRoomEventHandler
import javax.inject.Inject

class PlayerTeleportEventInteractor @Inject constructor(
    private val tpToArenaEventHandler: TpToArenaEventHandler,
    private val tpToWaitingRoomEventHandler: TpToWaitingRoomEventHandler
) {

    fun handle(event: PlayerTeleportEvent){
        tpToArenaEventHandler.setNext(tpToWaitingRoomEventHandler)
        tpToArenaEventHandler.handle(event)
    }

}