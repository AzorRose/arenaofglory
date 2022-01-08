package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.OutpostData

interface OutpostsRepository {

    fun getCachedOutposts(): Map<Long, OutpostData>

}