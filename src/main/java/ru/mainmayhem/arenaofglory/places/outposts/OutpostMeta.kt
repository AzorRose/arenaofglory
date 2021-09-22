package ru.mainmayhem.arenaofglory.places.outposts

import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta

class OutpostMeta(
    private val outpostId: Long,
    private val outpostName: String,
    private val outpostsRepository: OutpostsRepository
):ConquerablePlaceMeta()  {

    override fun defendingFractionId(): Long? {
        return outpostsRepository.getCachedOutposts()
            .find { it.first.id == outpostId }?.first?.fractionId
    }

    override fun getPlaceId(): Long = outpostId

    override fun getPlaceName(): String = outpostName

}