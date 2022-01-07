package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

/**
 * Проверка на "френдли файр"
 * Если игроки в одной фракции, матч идет и они в нем участвуют, то отменяем дамаг
 */
class FriendlyFireHandler @Inject constructor(
    @MatchJobInstance
    private val matchJob: PluginFiniteJob,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository
): BaseEventHandler<EntityDamageByEntityEvent>() {

    override fun handle(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val victim = event.entity
        if (damager !is Player || victim !is Player) {
            super.handle(event)
            return
        }
        if (damager inOneFractionWith victim && matchJob.isActive() && checkPlayersInArena(damager, victim)) {
            event.isCancelled = true
        } else {
            super.handle(event)
        }
    }

    private infix fun Player.inOneFractionWith(player: Player): Boolean {
        return arenaPlayersRepository.getCachedPlayerById(uniqueId.toString())?.fractionId ==
            arenaPlayersRepository.getCachedPlayerById(player.uniqueId.toString())?.fractionId
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
        val players = arenaMatchMetaRepository.getPlayers()
        return players.find { player -> player.player.id == uniqueId.toString() } != null
    }

}