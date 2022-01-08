package ru.mainmayhem.arenaofglory.places.outposts

import org.bukkit.ChatColor.GOLD
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus

private const val CAPTURE_DELAY_MILLIS = Constants.OUTPOST_CAPTURE_DELAY * Constants.MILLIS_IN_MINUTE

class OutpostMeta(
    private val outpostId: Long,
    private val outpostName: String,
    private val outpostsRepository: OutpostsRepository,
    private val javaPlugin: JavaPlugin
): ConquerablePlaceMeta() {

    var lastCaptureTime: Long? = null

    override fun defendingFractionId(): Long? {
        return outpostsRepository.getCachedOutposts()[outpostId]?.outpost?.fractionId
    }

    override fun getPlaceId(): Long = outpostId

    override fun getPlaceName(): String = outpostName

    override fun addPlayer(player: ArenaPlayer) {
        super.addPlayer(player)
        if (getStatus() !is ConquerablePlaceStatus.None && !canBeCaptured()) {
            javaPlugin.server.getPlayer(player.name)?.also {
                val minsFont = GOLD.toString()
                it.sendMessage(
                    "Данный аванпост находится под защитой, захватить его можно будет через $minsFont${getProtectedModeDuration()} мин"
                )
            }
            return
        }
    }

    fun canBeCaptured(): Boolean {
        val lastCapture = lastCaptureTime
        return lastCapture == null || (System.currentTimeMillis() - lastCapture) >= CAPTURE_DELAY_MILLIS
    }

    //в минутах
    fun getProtectedModeDuration(): Int {
        val lastCapture = lastCaptureTime ?: return 0
        val nextTimeCapture = lastCapture + CAPTURE_DELAY_MILLIS
        return ((nextTimeCapture - System.currentTimeMillis()) / Constants.MILLIS_IN_MINUTE).toInt().inc()
    }

}