package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.ArenaQueueDelayJob
import javax.inject.Inject

/**
 * Обработчик выхода игрока из комнаты ожидания
 * Если игрок вышел с сервера(неважно как) и в это время был в очереди на арену, то исключаем его
 */

class PlayerQuitWRQueue @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val logger: PluginLogger,
    private val arenaQueueDelayJob: ArenaQueueDelayJob,
    private val javaPlugin: JavaPlugin
): BaseEventHandler<PlayerEvent>() {

    override fun handle(event: PlayerEvent) {
        if (hasInQueue(event.player)){
            logger.info("Удаляем игрока ${event.player.getShortInfo()} из очереди на арену")
            arenaQueueRepository.remove(event.player.uniqueId.toString())
            //телепортируем игрока на спавн, чтобы при след. заходе он не оказался в комнате ожидания
            javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
                event.player.teleport(it.spawnLocation)
            }
        } else{
            logger.info("Игрок ${event.player.getShortInfo()} не найден в очереди на арену")
        }
        if (arenaQueueRepository.isEmpty())
            arenaQueueDelayJob.stop()
        super.handle(event)
    }

    private fun hasInQueue(player: Player): Boolean{
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(player.uniqueId.toString())
        if (arenaPlayer == null){
            logger.info("Игрок не является членом фракций")
            return false
        }
        val queue = arenaQueueRepository.get()
        return queue[arenaPlayer.fractionId]?.contains(arenaPlayer) == true
    }

}