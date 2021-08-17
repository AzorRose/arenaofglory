package ru.mainmayhem.arenaofglory.data.local.database.dao

import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

interface ArenaPlayersDao {

    suspend fun insert(player: ArenaPlayer)

    suspend fun getByPlayerId(playerId: String): ArenaPlayer

    suspend fun getByFractionId(fractionId: Long): List<ArenaPlayer>

    suspend fun getAll(): List<ArenaPlayer>

}