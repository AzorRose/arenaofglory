package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerKickEvent
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueue
import javax.inject.Inject

/**
 * Класс для создания цепочек для обработки кика игрока с сервера
 */
class PlayerKickedEventInteractor @Inject constructor(
    private val playerQuitWRQueue: PlayerQuitWRQueue,
    private val logger: PluginLogger
) {

    fun handle(event: PlayerKickEvent){
        logger.info("Игрок ${event.player.getShortInfo()} вышел с сервера")
        //todo добавить обработчик для арены
        playerQuitWRQueue.handle(event)
    }

}