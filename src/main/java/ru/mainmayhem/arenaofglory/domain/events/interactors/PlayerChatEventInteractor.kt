package ru.mainmayhem.arenaofglory.domain.events.interactors

import javax.inject.Inject
import org.bukkit.event.player.AsyncPlayerChatEvent
import ru.mainmayhem.arenaofglory.domain.events.EventHandler

class PlayerChatEventInteractor @Inject constructor(
    private val arenaChatEventHandler: EventHandler<AsyncPlayerChatEvent>
) {

    fun handle(event: AsyncPlayerChatEvent) {
        arenaChatEventHandler.handle(event)
    }

}