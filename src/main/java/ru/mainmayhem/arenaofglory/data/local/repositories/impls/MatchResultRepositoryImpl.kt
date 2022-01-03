package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.MatchResult
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.MatchResultsRepository
import javax.inject.Inject

class MatchResultRepositoryImpl @Inject constructor(
    coroutineScope: CoroutineScope,
    database: PluginDatabase
): MatchResultsRepository {

    private val matchResultsDao = database.getMatchResultsDao()
    private val cache = mutableListOf<MatchResult>()

    init {
        coroutineScope.launch { cache.addAll(matchResultsDao.getAll()) }
    }

    override fun getCachedResults(): List<MatchResult> = cache.toList()

    override suspend fun addResult(winnerFractionId: Long, looserFractionId: Long) {
        cache.add(MatchResult(winnerFractionId, looserFractionId))
        matchResultsDao.add(winnerFractionId, looserFractionId)
    }

}