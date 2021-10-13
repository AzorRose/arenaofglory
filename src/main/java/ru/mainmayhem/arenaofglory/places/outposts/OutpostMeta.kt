package ru.mainmayhem.arenaofglory.places.outposts

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus
import java.util.*

class OutpostMeta(
    private val outpostId: Long,
    private val outpostName: String,
    private val outpostsRepository: OutpostsRepository,
    private val javaPlugin: JavaPlugin
):ConquerablePlaceMeta()  {

    var lastCaptureTime: Long? = null

    override fun defendingFractionId(): Long? {
        return outpostsRepository.getCachedOutposts()
            .find { it.first.id == outpostId }?.first?.fractionId
    }

    override fun getPlaceId(): Long = outpostId

    override fun getPlaceName(): String = outpostName

    override fun addPlayer(player: ArenaPlayer) {
        super.addPlayer(player)
        if (getStatus() !is ConquerablePlaceStatus.None && !canBeCaptured()){
            javaPlugin.server.getPlayer(player.name)?.also {
                it.sendMessage(
                    "Данный аванпост находится под защитой, захватить его можно будет через ${getProtectedModeDuration()} мин"
                )
            }
            return
        }
    }

    fun canBeCaptured(): Boolean{
        val lastCapture = lastCaptureTime
        return lastCapture == null || (Date().time - lastCapture) >= Constants.OUTPOST_CAPTURE_DELAY * 60_000
    }

    //в минутах
    fun getProtectedModeDuration(): Int{
        val lastCapture = lastCaptureTime ?: return 0
        val nextTimeCapture = lastCapture + Constants.OUTPOST_CAPTURE_DELAY * 60_000
        return ((nextTimeCapture - Date().time) / 60_000).toInt().inc()
    }

}