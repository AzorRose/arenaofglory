package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.RespawnCoordinates

interface ArenaRespawnCoordinatesDao {

    suspend fun insert(respawnCoordinates: List<RespawnCoordinates>)

    suspend fun get(): List<RespawnCoordinates>

    suspend fun coordinatesFlow(): Flow<List<RespawnCoordinates>>

    suspend fun isEmpty(): Boolean

}