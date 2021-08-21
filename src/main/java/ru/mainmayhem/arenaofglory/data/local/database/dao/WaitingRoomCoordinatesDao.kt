package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates

interface WaitingRoomCoordinatesDao {

    suspend fun insert(coordinates: LocationCoordinates)

    suspend fun get(): LocationCoordinates?

    suspend fun locationFlow(): Flow<LocationCoordinates?>

    suspend fun isEmpty(): Boolean

}