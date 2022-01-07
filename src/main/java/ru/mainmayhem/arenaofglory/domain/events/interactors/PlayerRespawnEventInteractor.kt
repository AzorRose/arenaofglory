package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerRespawnEvent
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

/**
 * Класс для создания цепочек для обработки респавна игрока
 */
class PlayerRespawnEventInteractor @Inject constructor(
    private val arenaRespawnEventHandler: EventHandler<PlayerRespawnEvent>
) {

    fun handle(event: PlayerRespawnEvent) {
        arenaRespawnEventHandler.handle(event)
    }

}