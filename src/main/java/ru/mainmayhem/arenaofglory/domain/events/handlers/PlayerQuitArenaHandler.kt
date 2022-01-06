package ru.mainmayhem.arenaofglory.domain.events.handlers

import java.util.UUID
import javax.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.dagger.annotations.EmptyTeamJobInstance
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.DisbalanceFinder
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

/**
 * Обработчик выхода игрока из арены
 * Если игрок вышел с сервера(неважно как) и в это время был на арене, то:
 * Удаляем его из текущей сессии арены
 * Добавляем нового чувака из очереди и телепортируем его на спавн
 * Если очередь фракции пуста, запускаем таймер на 1 минуту
 */
class PlayerQuitArenaHandler @Inject constructor(
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val respawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    @EmptyTeamJobInstance
    private val emptyTeamJob: PluginFiniteJob,
    private val disbalanceFinder: DisbalanceFinder
): BaseEventHandler<PlayerEvent>() {

    override fun handle(event: PlayerEvent) {
        if (hasInArena(event.player)){
            logger.info("Удаляем игрока ${event.player.getShortInfo()} из участников арены")
            val playerId = event.player.uniqueId.toString()
            arenaMatchMetaRepository.remove(playerId)
            val fractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
            if (fractionId == null){
                logger.warning("Невозможно взять нового игрока из очереди: не найден id фракции")
                super.handle(event)
                return
            }
            val newPlayer = arenaQueueRepository.getAndRemove(fractionId)
            if (newPlayer == null){
                logger.info("Невозможно взять нового игрока из очереди: очередь фракции пуста")
            }
            newPlayer?.let {
                arenaMatchMetaRepository.insert(newPlayer)
            }
            newPlayer?.teleportPlayerToArena()
            //телепортируем игрока на спавн, чтобы при след. заходе он не оказался на арене
            javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
                event.player.teleport(it.spawnLocation)
            }
            if (disbalanceFinder.hasEmptyFractions()){
                logger.info("Обнаружена пустая команда, старт таймера автоматической победы")
                emptyTeamJob.start()
            }
        }
        super.handle(event)
    }

    private fun hasInArena(player: Player): Boolean{
        return arenaMatchMetaRepository.getPlayers().find {
            it.player.id == player.uniqueId.toString()
        } != null
    }

    private fun ArenaPlayer.teleportPlayerToArena(){
        val respawnCoordinates = respawnCoordinatesRepository.getCachedCoordinates()[fractionId]?.coordinates?.randomOrNull()
        if (respawnCoordinates == null){
            logger.warning("Невозможно переместить нового игрока на арену: не найдены точки респавна для фракции с id = $fractionId")
            return
        }
        val location = Location(
            javaPlugin.server.getWorld(Constants.WORLD_NAME),
            respawnCoordinates.x.toDouble(),
            respawnCoordinates.y.toDouble(),
            respawnCoordinates.z.toDouble()
        )
        javaPlugin.server.getPlayer(
            UUID.fromString(id)
        )?.teleport(location)
    }

}