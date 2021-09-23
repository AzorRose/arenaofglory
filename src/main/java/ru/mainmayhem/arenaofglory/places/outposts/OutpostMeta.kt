package ru.mainmayhem.arenaofglory.places.outposts

import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta
import java.util.*

class OutpostMeta(
    private val outpostId: Long,
    private val outpostName: String,
    private val outpostsRepository: OutpostsRepository
):ConquerablePlaceMeta()  {

    var lastCaptureTime: Long? = null

    override fun defendingFractionId(): Long? {
        return outpostsRepository.getCachedOutposts()
            .find { it.first.id == outpostId }?.first?.fractionId
    }

    override fun getPlaceId(): Long = outpostId

    override fun getPlaceName(): String = outpostName

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