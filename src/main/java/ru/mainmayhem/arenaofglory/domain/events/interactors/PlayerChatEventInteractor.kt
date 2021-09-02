package ru.mainmayhem.arenaofglory.domain.events.interactors

import org.bukkit.event.player.AsyncPlayerChatEvent
import ru.mainmayhem.arenaofglory.domain.events.handlers.ArenaChatEventHandler
import javax.inject.Inject

class PlayerChatEventInteractor @Inject constructor(
    private val arenaChatEventHandler: ArenaChatEventHandler
) {

    fun handle(event: AsyncPlayerChatEvent){
        arenaChatEventHandler.handle(event)
    }

}