package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchJobInstance
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

/**
 * Класс-обработчик для реализации командного чата внутри арены
 */
class ArenaChatEventHandler @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val javaPlugin: JavaPlugin,
    @MatchJobInstance
    private val matchJob: PluginFiniteJob
): BaseEventHandler<AsyncPlayerChatEvent>() {

    override fun handle(event: AsyncPlayerChatEvent) {

        if (!matchJob.isActive()) {
            super.handle(event)
            return
        }

        val player = event.player
        if (player.isNotInArena()) {
            super.handle(event)
            return
        }

        player.sendMessageToTeammates(event.message)

        event.isCancelled = true

    }

    private fun Player.isNotInArena(): Boolean {
        return arenaMatchMetaRepository.getPlayers().find { matchMember ->
            matchMember.player.id == uniqueId.toString()
        } == null
    }

    private fun Player.sendMessageToTeammates(message: String) {
        val players = arenaMatchMetaRepository.getPlayers()
        val playerId = uniqueId.toString()
        val playerFractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId ?: return
        players
            .filter { it.player.fractionId == playerFractionId }
            .forEach { matchMember ->
                javaPlugin.server.getPlayer(matchMember.player.name)?.sendMessage("$playerListName: $message")
            }
    }

}