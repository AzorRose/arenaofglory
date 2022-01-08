package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler

private const val INVALID_ID = -1L

/**
 * Обработчик убийства игрока на арене
 */
class ArenaKillingEventHandler @Inject constructor(
    private val logger: PluginLogger,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository
): BaseEventHandler<PlayerDeathEvent>() {

    override fun handle(event: PlayerDeathEvent) {
        val killed = event.entity
        val killer = event.entity.killer
        if (killer == null) {
            super.handle(event)
            return
        }
        if (checkPlayersInArena(killed, killer)) {
            arenaMatchMetaRepository.incrementPlayerKills(killer.uniqueId.toString())
            arenaMatchMetaRepository.increaseFractionPoints(
                fractionId = getFractionId(killer.uniqueId.toString()),
                points = Constants.FRACTION_KILL_PONTS
            )
        }
        super.handle(event)
    }

    private fun getFractionId(playerId: String): Long {
        val id = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
        if (id == null) {
            logger.error(
                className = "ArenaKillingEventHandler",
                methodName = "handle",
                throwable = NullPointerException(
                    "Невозможно увеличить очко фракции: фракция не найдена для игрока с id = $playerId"
                )
            )
        }
        return id ?: INVALID_ID
    }

    private fun checkPlayersInArena(vararg players: Player): Boolean {
        players.forEach { player ->
            if (!player.isInArena()) {
                return false
            }
        }
        return true
    }

    private fun Player.isInArena(): Boolean {
        return arenaMatchMetaRepository.getPlayerById(uniqueId.toString()) != null
    }

}