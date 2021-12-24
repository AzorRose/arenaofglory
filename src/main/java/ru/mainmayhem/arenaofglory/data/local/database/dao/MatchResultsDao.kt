package ru.mainmayhem.arenaofglory.data.local.database.dao

import ru.mainmayhem.arenaofglory.data.entities.MatchResult

interface MatchResultsDao {

    suspend fun getAll(): List<MatchResult>

    suspend fun add(winnerId: Long, looserId: Long)

}