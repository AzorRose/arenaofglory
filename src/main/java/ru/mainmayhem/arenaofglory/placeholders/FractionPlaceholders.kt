package ru.mainmayhem.arenaofglory.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import javax.inject.Inject

class FractionPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository
): PlaceholderExpansion() {

    override fun getIdentifier(): String = "fractions"

    override fun getAuthor(): String = "vkomarov"

    override fun getVersion(): String = "1.0.0"

    override fun canRegister(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return if (params == "arena_fraction_name"){
            player?.getFractionName().orEmpty()
        } else{
            ""
        }
    }

    private fun OfflinePlayer.getFractionName(): String{
        val id = uniqueId.toString()
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(id) ?: return ""
        return fractionsRepository.getCachedFractions()
            .find { it.id == arenaPlayer.fractionId }?.name
            .orEmpty()
    }

}