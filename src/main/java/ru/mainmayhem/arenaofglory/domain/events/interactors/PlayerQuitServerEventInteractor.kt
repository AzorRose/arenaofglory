package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitArenaHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitOutpostHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitWRQueueHandlerInstance
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

/**
 * Класс для создания цепочек для обработки выхода игрока с сервера
 */
class PlayerQuitServerEventInteractor @Inject constructor(
    @PlayerQuitWRQueueHandlerInstance
    private val playerQuitWRQueue: EventHandler<PlayerEvent>,
    @PlayerQuitArenaHandlerInstance
    private val playerQuitArena: EventHandler<PlayerEvent>,
    @PlayerQuitOutpostHandlerInstance
    private val playerQuitOutpostHandler: EventHandler<PlayerEvent>
) {

    fun handle(event: PlayerQuitEvent){
        playerQuitArena.setNext(playerQuitOutpostHandler)
        playerQuitWRQueue.setNext(playerQuitArena)
        playerQuitWRQueue.handle(event)
    }

}