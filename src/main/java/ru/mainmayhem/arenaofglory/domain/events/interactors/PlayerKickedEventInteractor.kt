package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerKickEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitArenaHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitOutpostHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitWRQueueHandlerInstance
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

/**
 * Класс для создания цепочек для обработки кика игрока с сервера
 */
class PlayerKickedEventInteractor @Inject constructor(
    @PlayerQuitWRQueueHandlerInstance
    private val playerQuitWRQueue: EventHandler<PlayerEvent>,
    @PlayerQuitArenaHandlerInstance
    private val playerQuitArenaHandler: EventHandler<PlayerEvent>,
    @PlayerQuitOutpostHandlerInstance
    private val playerQuitOutpostHandler: EventHandler<PlayerEvent>,
    private val logger: PluginLogger
) {

    fun handle(event: PlayerKickEvent) {
        logger.info("Игрок ${event.player.getShortInfo()} вышел с сервера")
        playerQuitArenaHandler.setNext(playerQuitOutpostHandler)
        playerQuitWRQueue.setNext(playerQuitArenaHandler)
        playerQuitWRQueue.handle(event)
    }

}