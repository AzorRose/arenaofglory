package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.FriendlyFireHandler
import javax.inject.Inject

/**
 * Класс для создания цепочек для обработки атак одних сущностей на других
 */
class PlayerDamageEventInteractor @Inject constructor(
    private val friendlyFireHandler: FriendlyFireHandler
) {

    fun handle(event: EntityDamageByEntityEvent){
        friendlyFireHandler.handle(event)
    }

}