package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerQuitEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitArenaHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitOutpostHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueueHandler

/**
 * Класс для создания цепочек для обработки выхода игрока с сервера
 */
class PlayerQuitServerEventInteractor @Inject constructor(
    private val playerQuitWRQueue: PlayerQuitWRQueueHandler,
    private val playerQuitArena: PlayerQuitArenaHandler,
    private val playerQuitOutpostHandler: PlayerQuitOutpostHandler
) {

    fun handle(event: PlayerQuitEvent){
        playerQuitArena.setNext(playerQuitOutpostHandler)
        playerQuitWRQueue.setNext(playerQuitArena)
        playerQuitWRQueue.handle(event)
    }

}