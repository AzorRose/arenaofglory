package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.PlayerRespawnEvent
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaRespawnEventHandler
import javax.inject.Inject

/**
 * Класс для создания цепочек для обработки респавна игрока
 */
class PlayerRespawnEventInteractor @Inject constructor(
    private val logger: PluginLogger,
    private val arenaRespawnEventHandler: ArenaRespawnEventHandler
) {

    fun handle(event: PlayerRespawnEvent){
        logger.info("Игрок ${event.player.getShortInfo()} респавнится")
        arenaRespawnEventHandler.handle(event)
    }

}