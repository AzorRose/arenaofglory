package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerQuitEvent
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitArenaHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueueHandler
import javax.inject.Inject

/**
 * Класс для создания цепочек для обработки выхода игрока с сервера
 */
class PlayerQuitServerEventInteractor @Inject constructor(
    private val playerQuitWRQueue: PlayerQuitWRQueueHandler,
    private val playerQuitArena: PlayerQuitArenaHandler,
    private val logger: PluginLogger
) {

    fun handle(event: PlayerQuitEvent){
        logger.info("Игрок ${event.player.getShortInfo()} вышел с сервера")
        playerQuitWRQueue.apply {
            setNext(playerQuitArena)
        }.handle(event)
    }

}