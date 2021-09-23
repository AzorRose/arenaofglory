package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.Outpost

interface OutpostsDao {

    suspend fun insert(outposts: List<Outpost>)

    suspend fun get(): List<Outpost>

    suspend fun coordinatesFlow(): Flow<List<Outpost>>

    suspend fun isEmpty(): Boolean

    suspend fun changeOwner(outpostId: Long, ownerFractionId: Long)

}