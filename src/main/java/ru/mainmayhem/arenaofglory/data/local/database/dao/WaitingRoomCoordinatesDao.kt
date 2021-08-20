package ru.mainmayhem.arenaofglory.data.local.database.dao

import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates

interface WaitingRoomCoordinatesDao {

    suspend fun insert(coordinates: LocationCoordinates)

    suspend fun get(): LocationCoordinates

    suspend fun isEmpty(): Boolean

}