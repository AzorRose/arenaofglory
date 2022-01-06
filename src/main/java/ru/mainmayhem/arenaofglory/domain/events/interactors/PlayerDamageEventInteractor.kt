package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

/**
 * Класс для создания цепочек для обработки атак одних сущностей на других
 */
class PlayerDamageEventInteractor @Inject constructor(
    private val friendlyFireHandler: EventHandler<EntityDamageByEntityEvent>
) {

    fun handle(event: EntityDamageByEntityEvent) {
        friendlyFireHandler.handle(event)
    }

}