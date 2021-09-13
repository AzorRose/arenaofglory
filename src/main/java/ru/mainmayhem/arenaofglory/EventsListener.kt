package ru.mainmayhem.arenaofglory

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.interactors.*
import javax.inject.Inject


/**
 * Класс который ловит ВСЕ события и делегирует работу другим классам
 * !не должен содержать никакой логики!
 */
class EventsListener @Inject constructor(
    private val playerQuitServerEventInteractor: PlayerQuitServerEventInteractor,
    private val playerKickedEventInteractor: PlayerKickedEventInteractor,
    private val playerDamageEventInteractor: PlayerDamageEventInteractor,
    private val playerRespawnEventInteractor: PlayerRespawnEventInteractor,
    private val playerDeathEventInteractor: PlayerDeathEventInteractor,
    private val playerChatEventInteractor: PlayerChatEventInteractor,
    private val playerMoveEventInteractor: PlayerMoveEventInteractor,
    private val playerTeleportEventInteractor: PlayerTeleportEventInteractor,
    private val logger: PluginLogger
): Listener {

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent){
        playerKickedEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        playerQuitServerEventInteractor.handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerRespawn(event: PlayerRespawnEvent){
        playerRespawnEventInteractor.handle(event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDamaged(event: EntityDamageByEntityEvent){
        //playerDamageEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerKilled(event: PlayerDeathEvent){
        playerDeathEventInteractor.handle(event)
    }

    @EventHandler
    fun onChatMessage(event: AsyncPlayerChatEvent){
        val fraction = "fraction = %arena_fraction_name%"
        logger.warning(PlaceholderAPI.setPlaceholders(event.player, fraction))
        playerChatEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerMoved(event: PlayerMoveEvent){
        playerMoveEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerTeleported(event: PlayerTeleportEvent){
        playerTeleportEventInteractor.handle(event)
    }

}