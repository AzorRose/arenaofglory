package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.entity.PlayerDeathEvent
import ru.mainmayhem.arenaofglory.data.dagger.annotations.ArenaKillingEventHandlerInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.ArenaSuicideEventHandlerInstance
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

class PlayerDeathEventInteractor @Inject constructor(
    @ArenaKillingEventHandlerInstance
    private val arenaKillingEventHandler: EventHandler<PlayerDeathEvent>,
    @ArenaSuicideEventHandlerInstance
    private val arenaSuicideEventHandler: EventHandler<PlayerDeathEvent>
) {

    fun handle(event: PlayerDeathEvent) {
        arenaSuicideEventHandler.setNext(arenaKillingEventHandler)
        arenaSuicideEventHandler.handle(event)
    }

}