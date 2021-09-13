package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import javax.inject.Inject

class ArenaSuicideEventHandler @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val logger: PluginLogger,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository
): BaseEventHandler<PlayerDeathEvent>() {

    override fun handle(event: PlayerDeathEvent) {
        val killed = event.entity
        val killer = event.entity.killer
        if (killer != null && killer.uniqueId != killed.uniqueId){
            super.handle(event)
            return
        }
        val playerFractionId = getFractionId(killed.uniqueId.toString())
        if (killed.isInArena()){
            fractionsRepository.getCachedFractions().forEach {
                if (it.id != playerFractionId){
                    arenaMatchMetaRepository.increaseFractionPoints(
                        fractionId = it.id,
                        points = Constants.FRACTION_KILL_PONTS
                    )
                }
            }
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

    private fun Player.isInArena(): Boolean{
        val players = arenaMatchMetaRepository.getPlayers()
        return players.find { it.player.id == uniqueId.toString() } != null
    }

}