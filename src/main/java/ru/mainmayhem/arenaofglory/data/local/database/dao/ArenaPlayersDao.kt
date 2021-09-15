package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

interface ArenaPlayersDao {

    suspend fun insert(player: ArenaPlayer)

    suspend fun updateFraction(playerName: String, newFractionId: Long)

    suspend fun getByPlayerId(playerId: String): ArenaPlayer?

    suspend fun getByFractionId(fractionId: Long): List<ArenaPlayer>

    suspend fun getAll(): List<ArenaPlayer>

    suspend fun getPlayersFlow(): Flow<List<ArenaPlayer>>

}