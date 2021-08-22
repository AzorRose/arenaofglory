package ru.mainmayhem.arenaofglory

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.domain.events.interactors.PlayerQuitServerEventInteractor
import javax.inject.Inject


/**
 * Класс который ловит ВСЕ события и делегирует работу другим классам
 * !не должен содержать никакой логики!
 */
class EventsListener @Inject constructor(
    private val playerQuitServerEventInteractor: PlayerQuitServerEventInteractor
): Listener {

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent){
        //todo когда игрока кикают и он в это время был на арене, нужно достать из очереди чувака его фракции
        //todo если он вышел из комнаты ожидания, то исключить его из очереди
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        playerQuitServerEventInteractor.handle(event)
    }

    @EventHandler
    fun onPlayerTeleported(event: PlayerTeleportEvent){
        //todo если телепортировался из "внешнего мира" в комнату ожидания - кидаем в очередь
        //todo если телепортировался из арены в комнату ожидания - выдать награду и телепортировать во внешний мир
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent){
//        event.respawnLocation =
        //todo если игрок умер на арене, его респавнит на базу его фракции
    }

    @EventHandler
    fun onPlayerDamaged(entity: EntityDamageEvent){
        //todo проверяем если урон был нанесен в комнате ожидания, то отменяем
    }

    @EventHandler
    fun onPlayerKilled(event: PlayerDeathEvent){
        //todo проверить, что игрок был убит на арене и прибавить очки противоположной команде
        //todo убийце увеличить счетчик убийств
//        val killed: String = e.getEntity().getName()
//        val killer: String = e.getEntity().getKiller().getName()
//        e.setDeathMessage(ChatColor.RED.toString() + killed + " has been slain by " + killer)
    }

}