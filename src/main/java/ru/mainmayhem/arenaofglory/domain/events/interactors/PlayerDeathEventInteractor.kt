package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.entity.PlayerDeathEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaKillingEventHandler
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaSuicideEventHandler
import javax.inject.Inject

class PlayerDeathEventInteractor @Inject constructor(
    private val arenaKillingEventHandler: ArenaKillingEventHandler,
    private val arenaSuicideEventHandler: ArenaSuicideEventHandler
) {

    fun handle(event: PlayerDeathEvent){
        arenaSuicideEventHandler.setNext(arenaKillingEventHandler)
        arenaSuicideEventHandler.handle(event)
    }

}