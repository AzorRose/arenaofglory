package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerEvent
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder

/**
 * Удаление игрока с аванопостов, если он вышел с сервера или его кикнули
 */
class PlayerQuitOutpostHandler @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val outpostsRepository: OutpostsRepository,
    private val outpostsHolder: OutpostsHolder
): BaseEventHandler<PlayerEvent>() {

    override fun handle(event: PlayerEvent) {
        val player = arenaPlayersRepository.getCachedPlayerById(event.player.uniqueId.toString())
        if (player == null) {
            super.handle(event)
            return
        }
        outpostsRepository.getCachedOutposts().forEach {
            outpostsHolder.removePlayer(player, it.first.id)
        }
        super.handle(event)
    }

}