package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerKickEvent
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitArenaHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitOutpostHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueueHandler

/**
 * Класс для создания цепочек для обработки кика игрока с сервера
 */
class PlayerKickedEventInteractor @Inject constructor(
    private val playerQuitWRQueue: PlayerQuitWRQueueHandler,
    private val playerQuitArenaHandler: PlayerQuitArenaHandler,
    private val playerQuitOutpostHandler: PlayerQuitOutpostHandler,
    private val logger: PluginLogger
) {

    fun handle(event: PlayerKickEvent){
        logger.info("Игрок ${event.player.getShortInfo()} вышел с сервера")
        playerQuitArenaHandler.setNext(playerQuitOutpostHandler)
        playerQuitWRQueue.setNext(playerQuitArenaHandler)
        playerQuitWRQueue.handle(event)
    }

}