package ru.mainmayhem.arenaofglory.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

class PlayersPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val queueRepository: ArenaQueueRepository,
    private val logger: PluginLogger
): PlaceholderExpansion() {

    private val totalKillsPlaceholder = "totalkills"
    private val queuePosition = "queueposition"

    override fun getIdentifier(): String = "player"

    override fun getAuthor(): String = "vkomarov"

    override fun getVersion(): String = "1.0.1"

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when (params) {
            totalKillsPlaceholder -> player?.getTotalKills().orEmpty()
            queuePosition -> player?.getQueuePosition().toString()
            else -> ""
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun OfflinePlayer.getTotalKills(): String{
        val id = uniqueId.toString()
        return (arenaPlayersRepository.getCachedPlayerById(id)?.kills ?: 0).toString()
    }

    private fun OfflinePlayer.getQueuePosition(): String{
        val queue = queueRepository.get()
        val fractionId = arenaPlayersRepository.getCachedPlayerById(uniqueId.toString())?.fractionId ?: return ""
        val min = getMinPlayersInFraction(queue)
        val position = queue[fractionId]?.indexOfFirst { it.id == uniqueId.toString() }
        if (position == null){
            logger.warning("Фракция с id = $fractionId не найдена в очереди")
            return "Проходите"
        }
        if (position == -1){
            logger.warning("Игрок $name не найден в очереди")
            return ""
        }
        return if (position < min) "Проходите" else position.inc().toString()
    }

    private fun getMinPlayersInFraction(players: Map<Long, Set<ArenaPlayer>>): Int {
        var res = 0
        players.entries.forEachIndexed { index, entry ->
            val size = entry.value.size
            when{
                index == 0 -> res = entry.value.size
                res > size -> res = size
            }
        }
        return res
    }

}