package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.Outpost
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation

interface OutpostsRepository {

    fun getCachedOutposts(): List<Pair<Outpost, CalculatedLocation>>

}