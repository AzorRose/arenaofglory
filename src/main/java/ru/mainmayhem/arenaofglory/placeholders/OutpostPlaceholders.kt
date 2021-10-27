package ru.mainmayhem.arenaofglory.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder
import javax.inject.Inject

class OutpostPlaceholders @Inject constructor(
    private val outpostsHolder: OutpostsHolder,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger
): PlaceholderExpansion() {

    private val ownerPlaceholder = "owner"
    private val percentPlaceholder = "percent"
    private val statusPlaceholder = "status"

    override fun getIdentifier(): String = "outposts"

    override fun getAuthor(): String = "vkomarov"

    override fun getVersion(): String = "1.0"

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        return when{
            params.startsWith(ownerPlaceholder) -> getOwnerByParams(params)
            params.startsWith(percentPlaceholder) -> getPercentStatusByParams(params)
            params.startsWith(statusPlaceholder) -> getStatusByParams(params)
            else -> ""
        }
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return onRequest(player, params)
    }

    private fun getStatusByParams(params: String): String{
        val id = params.replace(statusPlaceholder, "").toLongOrNull()
        if (id == null){
            logger.warning("Некорректный id аванпоста: $id")
            return ""
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null){
            logger.warning("Аванпост с id = $id не найден")
            return ""
        }
        val underAttack = "UnderAttack"
        val readyForFight = "ReadyForFight"
        val underCover = "UnderCover"
        return when{
            //Идет захват (когда кто-то из вражеской фракции находится на территории аванпоста и пытается его захватить)
            outpost.getStatus() !is ConquerablePlaceStatus.None -> underAttack
            //Готов к бою (Когда время щита прошло, но никто ещё не захватывает)
            outpost.canBeCaptured() && outpost.getStatus() is ConquerablePlaceStatus.None -> readyForFight
            //Под защитой (После захвата аванпоста на 1 час накладывается щит, запрещающий обратный захват)
            !outpost.canBeCaptured() -> underCover
            else -> ""
        }
    }

    private fun getPercentStatusByParams(params: String): String{
        val id = params.replace(percentPlaceholder, "").toLongOrNull()
        if (id == null){
            logger.warning("Некорректный id аванпоста: $id")
            return ""
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null){
            logger.warning("Аванпост с id = $id не найден")
            return ""
        }
        return outpost.getState().toString()
    }

    private fun getOwnerByParams(params: String): String{
        val id = params.replace(ownerPlaceholder, "").toLongOrNull()
        if (id == null){
            logger.warning("Некорректный id аванпоста: $id")
            return ""
        }
        val outpost = outpostsHolder.getOutpostMeta(id)
        if (outpost == null){
            logger.warning("Аванпост с id = $id не найден")
            return ""
        }
        val fractionId = outpost.defendingFractionId() ?: return ""
        val fraction = fractionsRepository.getCachedFractions().find { it.id == fractionId }
        if (fraction == null){
            logger.warning("Не удалось найти фракцию с id = $fractionId")
            return ""
        }
        return fraction.name
    }

}