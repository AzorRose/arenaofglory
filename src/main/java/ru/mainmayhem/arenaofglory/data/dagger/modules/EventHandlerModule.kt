package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.ArenaKillingEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.ArenaSuicideEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MoveToEnemyRespawnEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerEnteredOutpostEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerLeftOutpostEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitArenaHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.PlayerQuitWRQueueHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToArenaEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToOutpostsEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.TpToWaitingRoomEventHandlerInstance
import ru.mainmayhem.arenaofglory.domain.events.EventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaChatEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaKillingEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaRespawnEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaSuicideEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.FriendlyFireHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.MoveToEnemyRespawnEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerEnteredOutpostEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerLeftOutpostEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitArenaHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.PlayerQuitWRQueueHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToArenaEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToOutpostsEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.TpToWaitingRoomEventHandler

@Module
abstract class EventHandlerModule {

    @Binds
    abstract fun getArenaChatEventHandler(impl: ArenaChatEventHandler): EventHandler<AsyncPlayerChatEvent>

    @Binds
    @ArenaKillingEventHandlerInstance
    abstract fun getArenaKillingEventHandler(impl: ArenaKillingEventHandler): EventHandler<PlayerDeathEvent>

    @Binds
    @ArenaSuicideEventHandlerInstance
    abstract fun getArenaSuicideEventHandler(impl: ArenaSuicideEventHandler): EventHandler<PlayerDeathEvent>

    @Binds
    abstract fun getFriendlyFireHandler(impl: FriendlyFireHandler): EventHandler<EntityDamageByEntityEvent>

    @Binds
    @PlayerQuitWRQueueHandlerInstance
    abstract fun getPlayerQuitWRQueueHandler(impl: PlayerQuitWRQueueHandler): EventHandler<PlayerEvent>

    @Binds
    @PlayerQuitArenaHandlerInstance
    abstract fun getPlayerQuitArenaHandler(impl: PlayerQuitArenaHandler): EventHandler<PlayerEvent>

    @Binds
    @MoveToEnemyRespawnEventHandlerInstance
    abstract fun getMoveToEnemyRespawnEventHandler(impl: MoveToEnemyRespawnEventHandler): EventHandler<PlayerMoveEvent>

    @Binds
    @PlayerEnteredOutpostEventHandlerInstance
    abstract fun getPlayerEnteredOutpostEventHandler(impl: PlayerEnteredOutpostEventHandler): EventHandler<PlayerMoveEvent>

    @Binds
    @PlayerLeftOutpostEventHandlerInstance
    abstract fun getPlayerLeftOutpostEventHandler(impl: PlayerLeftOutpostEventHandler): EventHandler<PlayerMoveEvent>

    @Binds
    abstract fun getArenaRespawnEventHandler(impl: ArenaRespawnEventHandler): EventHandler<PlayerRespawnEvent>

    @Binds
    @TpToArenaEventHandlerInstance
    abstract fun getTpToArenaEventHandler(impl: TpToArenaEventHandler): EventHandler<PlayerTeleportEvent>

    @Binds
    @TpToWaitingRoomEventHandlerInstance
    abstract fun getTpToWaitingRoomEventHandler(impl: TpToWaitingRoomEventHandler): EventHandler<PlayerTeleportEvent>

    @Binds
    @TpToOutpostsEventHandlerInstance
    abstract fun getTpToOutpostsEventHandler(impl: TpToOutpostsEventHandler): EventHandler<PlayerTeleportEvent>

}