package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import java.util.*
import javax.inject.Inject

/**
 * Обработчик убийства игрока на арене
 */
class ArenaKillingEventHandler @Inject constructor(
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val javaPlugin: JavaPlugin
): BaseEventHandler<PlayerDeathEvent>() {

    override fun handle(event: PlayerDeathEvent) {
        val killed = event.entity
        val killer = event.entity.killer
        if (killer == null){
            super.handle(event)
            return
        }
        if (checkPlayersInArena(killed, killer)){
            sendMessageToAllPlayersInMatch(
                "Игрок ${killer.playerListName} убивает ${killed.playerListName}"
            )
            arenaMatchMetaRepository.incrementPlayerKills(killer.uniqueId.toString())
            arenaMatchMetaRepository.increaseFractionPoints(
                fractionId = getFractionId(killer.uniqueId.toString()),
                points = Constants.FRACTION_KILL_PONTS
            )
        }
        super.handle(event)
    }

    private fun getFractionId(playerId: String): Long{
        val id = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
        if (id == null){
            logger.error(
                className = "ArenaKillingEventHandler",
                methodName = "handle",
                throwable = NullPointerException(
                    "Невозможно увеличить очко фракции: фракция не найдена для игрока с id = $playerId"
                )
            )
        }
        return id ?: -1
    }

    private fun checkPlayersInArena(vararg players: Player): Boolean {
        players.forEach { player ->
            if (!player.isInArena()){
                return false
            }
        }
        return true
    }

    private fun Player.isInArena(): Boolean{
        val players = arenaMatchMetaRepository.getPlayers()
        return players.find { it.player.id == uniqueId.toString() } != null
    }

    private fun sendMessageToAllPlayersInMatch(message: String){
        arenaMatchMetaRepository.getPlayers().forEach {
            javaPlugin.server.getPlayer(
                UUID.fromString(it.player.id)
            )?.sendMessage(message)
        }
    }

}