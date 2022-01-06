package ru.mainmayhem.arenaofglory.placeholders

import javax.inject.Inject
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val EMPTY_RESULT = ""
private const val PLACEHOLDER_IDENTIFIER = "player"
private const val PLACEHOLDER_AUTHOR = "vkomarov"
private const val PLACEHOLDER_VERSION = "1.0"
private const val PLACEHOLDER_TOTAL_KILLS = "totalkills"
private const val PLACEHOLDER_QUEUE_POSITION = "queueposition"
private const val DEFAULT_KILLS_AMOUNT = 0
private const val INVALID_ITEM_POSITION = -1

class PlayersPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val queueRepository: ArenaQueueRepository,
    private val logger: PluginLogger
): PlaceholderExpansion() {

    override fun getIdentifier(): String = PLACEHOLDER_IDENTIFIER

    override fun getAuthor(): String = PLACEHOLDER_AUTHOR

    override fun getVersion(): String = PLACEHOLDER_VERSION

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when (params) {
            PLACEHOLDER_TOTAL_KILLS -> player?.getTotalKills().orEmpty()
            PLACEHOLDER_QUEUE_POSITION -> player?.getQueuePosition().toString()
            else -> EMPTY_RESULT
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun OfflinePlayer.getTotalKills(): String {
        val id = uniqueId.toString()
        val kills = arenaPlayersRepository.getCachedPlayerById(id)?.kills ?: DEFAULT_KILLS_AMOUNT
        return kills.toString()
    }

    private fun OfflinePlayer.getQueuePosition(): String {
        val queue = queueRepository.get()
        val fractionId =
            arenaPlayersRepository.getCachedPlayerById(uniqueId.toString())?.fractionId ?: return EMPTY_RESULT
        val min = getMinPlayersInFraction(queue)
        val position = queue[fractionId]?.indexOfFirst { it.id == uniqueId.toString() }
        if (position == null) {
            logger.warning("Фракция с id = $fractionId не найдена в очереди")
            return "Проходите"
        }
        if (position == INVALID_ITEM_POSITION) {
            logger.warning("Игрок $name не найден в очереди")
            return EMPTY_RESULT
        }
        return if (position < min) "Проходите" else position.inc().toString()
    }

    private fun getMinPlayersInFraction(players: Map<Long, Set<ArenaPlayer>>): Int {
        var res = 0
        players.entries.forEachIndexed { index, entry ->
            val size = entry.value.size
            when {
                index == 0 -> res = entry.value.size
                res > size -> res = size
            }
        }
        return res
    }

}