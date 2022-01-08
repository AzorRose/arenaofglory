package ru.mainmayhem.arenaofglory.placeholders

import javax.inject.Inject
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val EMPTY_QUEUE_SIZE = 0
private const val EMPTY_RESULT = ""
private const val PLACEHOLDER_FRACTION_NAME = "name"
private const val PLACEHOLDER_FRACTION_QUEUE_AMOUNT = "queueamount"
private const val PLACEHOLDER_FRACTION_MEMBERS_AMOUNT = "membersamount"
private const val PLACEHOLDER_IDENTIFIER = "fractions"
private const val PLACEHOLDER_AUTHOR = "vkomarov"
private const val PLACEHOLDER_VERSION = "1.0"

class FractionPlaceholders @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger,
    private val queueRepository: ArenaQueueRepository
): PlaceholderExpansion() {

    override fun getIdentifier(): String = PLACEHOLDER_IDENTIFIER

    override fun getAuthor(): String = PLACEHOLDER_AUTHOR

    override fun getVersion(): String = PLACEHOLDER_VERSION

    override fun canRegister(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when {
            params == PLACEHOLDER_FRACTION_NAME -> player?.getFractionName().orEmpty()
            params.startsWith(PLACEHOLDER_FRACTION_QUEUE_AMOUNT) -> getQueueAmount(params)
            params.startsWith(PLACEHOLDER_FRACTION_MEMBERS_AMOUNT) -> getMembersAmount(params)
            else -> EMPTY_RESULT
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun getQueueAmount(rawParams: String): String {
        val fractionId = rawParams.replace(PLACEHOLDER_FRACTION_QUEUE_AMOUNT, "").toLongOrNull()
        if (fractionId == null) {
            logger.warning("Некорректный id фракции для плейсхолдера $fractionId")
            return EMPTY_RESULT
        }
        val queue = queueRepository.get()[fractionId]
        return (queue?.size ?: EMPTY_QUEUE_SIZE).toString()
    }

    private fun getMembersAmount(rawParams: String): String {
        val fractionId = rawParams.replace(PLACEHOLDER_FRACTION_MEMBERS_AMOUNT, "").toLongOrNull()
        if (fractionId == null) {
            logger.warning("Некорректный id фракции для плейсхолдера $fractionId")
            return EMPTY_RESULT
        }
        val players = arenaPlayersRepository.getCachedPlayers()
        return players.filter { it.fractionId == fractionId }.size.toString()
    }

    private fun OfflinePlayer.getFractionName(): String {
        val id = uniqueId.toString()
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(id) ?: return EMPTY_RESULT
        return fractionsRepository.getFractionById(arenaPlayer.fractionId)?.name.orEmpty()
    }

}