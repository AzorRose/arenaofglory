package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.MoveToEnemyRespawnEventHandler
import javax.inject.Inject

class PlayerMoveEventInteractor @Inject constructor(
    private val moveToEnemyRespawnEventHandler: MoveToEnemyRespawnEventHandler
) {

    fun handle(event: PlayerMoveEvent){
        moveToEnemyRespawnEventHandler.handle(event)
    }

}