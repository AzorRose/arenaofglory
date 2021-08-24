package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import javax.inject.Inject

/**
 * Обработчик выхода игрока из арены
 * Если игрок вышел с сервера(неважно как) и в это время был на арене, то удаляем его
 */
class PlayerQuitArenaHandler @Inject constructor(
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository
): BaseEventHandler<PlayerEvent>() {

    override fun handle(event: PlayerEvent) {
        if (hasInArena(event.player)){
            logger.info("Удаляем игрока ${event.player.getShortInfo()} из участников арены")
            arenaMatchMetaRepository.remove(event.player.uniqueId.toString())
            //телепортируем игрока на спавн, чтобы при след. заходе он не оказался на арене
            javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
                event.player.teleport(it.spawnLocation)
            }
        } else{
            logger.info("Игрок ${event.player.getShortInfo()} не является участником арены")
        }
        super.handle(event)
    }

    private fun hasInArena(player: Player): Boolean{
        return arenaMatchMetaRepository.getPlayers().find {
            it.player.id == player.uniqueId.toString()
        } != null
    }

}