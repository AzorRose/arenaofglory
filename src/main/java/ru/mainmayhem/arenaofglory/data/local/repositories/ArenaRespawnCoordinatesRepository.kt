package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.domain.CalculatedLocation

interface ArenaRespawnCoordinatesRepository {

    fun getCachedCoordinates(): Map<Long, CalculatedLocation>

}