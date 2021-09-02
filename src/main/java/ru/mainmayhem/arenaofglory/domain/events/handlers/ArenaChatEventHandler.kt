package ru.mainmayhem.arenaofglory.domain.events.handlers

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import java.util.*
import javax.inject.Inject

/**
 * Класс-обработчик для реализации командного чата внутри арены
 */
class ArenaChatEventHandler @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val javaPlugin: JavaPlugin,
    private val matchJob: MatchJob
): BaseEventHandler<AsyncPlayerChatEvent>() {

    override fun handle(event: AsyncPlayerChatEvent) {

        if (!matchJob.isActive){
            super.handle(event)
            return
        }

        val player = event.player
        if (player.isNotInArena()){
            super.handle(event)
            return
        }

        player.sendMessageToTeammates(event.message)

        event.isCancelled = true

    }

    private fun Player.isNotInArena(): Boolean{
        return arenaMatchMetaRepository.getPlayers().find {
            it.player.id == uniqueId.toString()
        } == null
    }

    private fun Player.sendMessageToTeammates(message: String){
        val players = arenaMatchMetaRepository.getPlayers()
        val playerId = uniqueId.toString()
        val playerFractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId ?: return
        players
            .filter { it.player.fractionId == playerFractionId }
            .forEach {
                javaPlugin.server.getPlayer(
                    UUID.fromString(it.player.id)
                )?.sendMessage("$playerListName: $message")
            }
    }

}