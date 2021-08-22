package ru.mainmayhem.arenaofglory.domain.useCases

import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueue
import javax.inject.Inject

/**
 * Логика начала матча, когда закончилось ожидание в 5 минут
 */
class ArenaQueueDelayCompletedUseCase @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val playerQuitWRQueue: PlayerQuitWRQueue
) {

    fun handle(){
        val size = queueSize()
        when(size){
            0 -> return
            1 -> kickPlayer()
        }
    }

    private fun kickPlayer(){
        val playerId = arenaQueueRepository.getAll().first().id
        val player = javaPlugin.server.getPlayer(playerId) ?: return
        playerQuitWRQueue.handle(PlayerEvent(player))
    }

    private fun startMatch(){

    }

    private fun queueSize(): Int{
        val queue = arenaQueueRepository.get()
        var res = 0
        queue.forEach { (_, value) ->
            res += value.size
        }
        return res
    }

}