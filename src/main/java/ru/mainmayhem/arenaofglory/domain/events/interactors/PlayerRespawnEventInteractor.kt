package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerRespawnEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaRespawnEventHandler
import javax.inject.Inject

/**
 * Класс для создания цепочек для обработки респавна игрока
 */
class PlayerRespawnEventInteractor @Inject constructor(
    private val arenaRespawnEventHandler: ArenaRespawnEventHandler
) {

    fun handle(event: PlayerRespawnEvent){
        arenaRespawnEventHandler.handle(event)
    }

}