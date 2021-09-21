package ru.mainmayhem.arenaofglory.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import javax.inject.Inject

class PlayersPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository
): PlaceholderExpansion() {

    private val totalKillsPlaceholder = "total_kills"

    override fun getIdentifier(): String = "players"

    override fun getAuthor(): String = "vkomarov"

    override fun getVersion(): String = "1.0.0"

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when{
            params == totalKillsPlaceholder -> player?.getTotalKills().orEmpty()
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

}