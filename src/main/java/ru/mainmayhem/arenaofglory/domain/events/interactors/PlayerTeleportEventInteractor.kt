package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToArenaEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToOutpostsEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToWaitingRoomEventHandlerInstance
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

class PlayerTeleportEventInteractor @Inject constructor(
    @TpToArenaEventHandlerInstance
    private val tpToArenaEventHandler: EventHandler<PlayerTeleportEvent>,
    @TpToWaitingRoomEventHandlerInstance
    private val tpToWaitingRoomEventHandler: EventHandler<PlayerTeleportEvent>,
    @TpToOutpostsEventHandlerInstance
    private val tpToOutpostsEventHandler: EventHandler<PlayerTeleportEvent>
) {

    fun handle(event: PlayerTeleportEvent) {
        tpToWaitingRoomEventHandler.setNext(tpToOutpostsEventHandler)
        tpToArenaEventHandler.setNext(tpToWaitingRoomEventHandler)
        tpToArenaEventHandler.handle(event)
    }

}