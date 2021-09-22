package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerMoveEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.MoveToEnemyRespawnEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerEnteredOutpostEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerLeftOutpostEventHandler
import javax.inject.Inject

class PlayerMoveEventInteractor @Inject constructor(
    private val moveToEnemyRespawnEventHandler: MoveToEnemyRespawnEventHandler,
    private val playerEnteredOutpostEventHandler: PlayerEnteredOutpostEventHandler,
    private val playerLeftOutpostEventHandler: PlayerLeftOutpostEventHandler
) {

    fun handle(event: PlayerMoveEvent){
        moveToEnemyRespawnEventHandler.setNext(playerEnteredOutpostEventHandler)
        playerEnteredOutpostEventHandler.setNext(playerLeftOutpostEventHandler)
        moveToEnemyRespawnEventHandler.handle(event)
    }

}