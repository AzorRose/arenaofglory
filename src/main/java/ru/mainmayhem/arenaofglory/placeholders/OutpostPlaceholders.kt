package ru.mainmayhem.arenaofglory.placeholders

import javax.inject.Inject
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder

private const val EMPTY_RESULT = ""
private const val PLACEHOLDER_OWNER = "owner"
private const val PLACEHOLDER_PERCENT = "percent"
private const val PLACEHOLDER_STATUS = "status"
private const val PLACEHOLDER_IDENTIFIER = "outposts"
private const val PLACEHOLDER_AUTHOR = "vkomarov"
private const val PLACEHOLDER_VERSION = "1.0"

class OutpostPlaceholders @Inject constructor(
    private val outpostsHolder: OutpostsHolder,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger
): PlaceholderExpansion() {

    override fun getIdentifier(): String = PLACEHOLDER_IDENTIFIER

    override fun getAuthor(): String = PLACEHOLDER_AUTHOR

    override fun getVersion(): String = PLACEHOLDER_VERSION

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when {
            params.startsWith(PLACEHOLDER_OWNER) -> getOwnerByParams(params)
            params.startsWith(PLACEHOLDER_PERCENT) -> getPercentStatusByParams(params)
            params.startsWith(PLACEHOLDER_STATUS) -> getStatusByParams(params)
            else -> EMPTY_RESULT
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun getStatusByParams(params: String): String {
        val id = params.replace(PLACEHOLDER_STATUS, "").toLongOrNull()
        if (id == null) {
            logger.warning("Некорректный id аванпоста: $id")
            return EMPTY_RESULT
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null) {
            logger.warning("Аванпост с id = $id не найден")
            return EMPTY_RESULT
        }
        val underAttack = "Сражение"
        val readyForFight = "Готов к бою"
        val underCover = "Под защитой"
        return when {
            //Идет захват (когда кто-то из вражеской фракции находится на территории аванпоста и пытается его захватить)
            outpost.getStatus() !is ConquerablePlaceStatus.None -> underAttack
            //Готов к бою (Когда время щита прошло, но никто ещё не захватывает)
            outpost.canBeCaptured() && outpost.getStatus() is ConquerablePlaceStatus.None -> readyForFight
            //Под защитой (После захвата аванпоста на 1 час накладывается щит, запрещающий обратный захват)
            !outpost.canBeCaptured() -> underCover
            else -> EMPTY_RESULT
        }
    }

    private fun getPercentStatusByParams(params: String): String {
        val id = params.replace(PLACEHOLDER_PERCENT, "").toLongOrNull()
        if (id == null) {
            logger.warning("Некорректный id аванпоста: $id")
            return EMPTY_RESULT
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null) {
            logger.warning("Аванпост с id = $id не найден")
            return EMPTY_RESULT
        }
        return outpost.getFormattedState()
    }

    private fun getOwnerByParams(params: String): String {
        val id = params.replace(PLACEHOLDER_OWNER, "").toLongOrNull()
        if (id == null) {
            logger.warning("Некорректный id аванпоста: $id")
            return EMPTY_RESULT
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null) {
            logger.warning("Аванпост с id = $id не найден")
            return EMPTY_RESULT
        }
        val fractionId = outpost.defendingFractionId() ?: return EMPTY_RESULT
        val fraction = fractionsRepository.getCachedFractions().find { it.id == fractionId }
        if (fraction == null) {
            logger.warning("Не удалось найти фракцию с id = $fractionId")
            return EMPTY_RESULT
        }
        return fraction.name
    }

}