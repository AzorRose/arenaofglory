package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.entity.PlayerDeathEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaKillingEventHandler
import javax.inject.Inject

class PlayerDeathEventInteractor @Inject constructor(
    private val arenaKillingEventHandler: ArenaKillingEventHandler
) {

    fun handle(event: PlayerDeathEvent){
        arenaKillingEventHandler.handle(event)
    }

}