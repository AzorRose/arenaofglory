package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.MatchResult

interface MatchResultsRepository {

    fun getCachedResults(): List<MatchResult>

    suspend fun addResult(winnerFractionId: Long, looserFractionId: Long)

    suspend fun addDrawResult()

}