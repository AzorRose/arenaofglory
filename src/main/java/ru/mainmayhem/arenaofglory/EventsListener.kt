package ru.mainmayhem.arenaofglory

import javax.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerChatEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerDamageEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerDeathEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerKickedEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerMoveEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerQuitServerEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerRespawnEventInteractor
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerTeleportEventInteractor

/**
 * Класс который ловит ВСЕ события и делегирует работу другим классам
 * !не должен содержать никакой логики!
 */
class EventsListener @Inject constructor(
    private val playerQuitServerEventInteractor: PlayerQuitServerEventInteractor,
    private val playerKickedEventInteractor: PlayerKickedEventInteractor,
    private val playerRespawnEventInteractor: PlayerRespawnEventInteractor,
    private val playerDeathEventInteractor: PlayerDeathEventInteractor,
    private val playerChatEventInteractor: PlayerChatEventInteractor,
    private val playerMoveEventInteractor: PlayerMoveEventInteractor,
    private val playerTeleportEventInteractor: PlayerTeleportEventInteractor,
    private val playerDamageEventInteractor: PlayerDamageEventInteractor
): Listener {

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent) {
        playerKickedEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        playerQuitServerEventInteractor.handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        playerRespawnEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerKilled(event: PlayerDeathEvent) {
        playerDeathEventInteractor.handle(event)
    }

    @EventHandler
    fun onChatMessage(event: AsyncPlayerChatEvent) {
        playerChatEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerMoved(event: PlayerMoveEvent) {
        playerMoveEventInteractor.handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerTeleported(event: PlayerTeleportEvent) {
        playerTeleportEventInteractor.handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamagedByEntity(event: EntityDamageByEntityEvent) {
        playerDamageEventInteractor.handle(event)
    }

}