package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MoveToEnemyRespawnEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerEnteredOutpostEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerLeftOutpostEventHandlerInstance
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

class PlayerMoveEventInteractor @Inject constructor(
    @MoveToEnemyRespawnEventHandlerInstance
    private val moveToEnemyRespawnEventHandler: EventHandler<PlayerMoveEvent>,
    @PlayerEnteredOutpostEventHandlerInstance
    private val playerEnteredOutpostEventHandler: EventHandler<PlayerMoveEvent>,
    @PlayerLeftOutpostEventHandlerInstance
    private val playerLeftOutpostEventHandler: EventHandler<PlayerMoveEvent>
) {

    fun handle(event: PlayerMoveEvent) {
        moveToEnemyRespawnEventHandler.setNext(playerEnteredOutpostEventHandler)
        playerEnteredOutpostEventHandler.setNext(playerLeftOutpostEventHandler)
        moveToEnemyRespawnEventHandler.handle(event)
    }

}