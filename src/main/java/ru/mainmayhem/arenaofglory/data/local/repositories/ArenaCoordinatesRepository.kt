package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.domain.CalculatedLocation

interface ArenaCoordinatesRepository {

    fun getCachedCoordinates(): CalculatedLocation?

}