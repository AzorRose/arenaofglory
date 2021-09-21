package ru.mainmayhem.arenaofglory.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

class FractionPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger,
    private val queueRepository: ArenaQueueRepository
): PlaceholderExpansion() {

    private val fractionNamePlaceholder = "name"
    private val fractionQueueAmountPlaceholder = "queueamount"

    override fun getIdentifier(): String = "fractions"

    override fun getAuthor(): String = "vkomarov"

    override fun getVersion(): String = "1.0.1"

    override fun canRegister(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when{
            params == fractionNamePlaceholder -> player?.getFractionName().orEmpty()
            params.startsWith(fractionQueueAmountPlaceholder) -> getQueueAmount(params)
            else -> ""
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun getQueueAmount(rawParams: String): String{
        val fractionId = rawParams.replace(fractionQueueAmountPlaceholder, "").toLongOrNull()
        if (fractionId == null){
            logger.warning("Некорректный id фракции для плейсхолдера $fractionId")
            return ""
        }
        val queue = queueRepository.get()[fractionId]
        return (queue?.size ?: 0).toString()
    }

    private fun OfflinePlayer.getFractionName(): String{
        val id = uniqueId.toString()
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(id) ?: return ""
        return fractionsRepository.getCachedFractions()
            .find { it.id == arenaPlayer.fractionId }?.name
            .orEmpty()
    }

}