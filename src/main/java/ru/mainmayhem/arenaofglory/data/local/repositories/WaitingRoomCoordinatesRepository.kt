package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.domain.CalculatedLocation

interface WaitingRoomCoordinatesRepository {

    fun getCachedCoordinates(): CalculatedLocation?

}