package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.MatchResult
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.MatchResultsRepository

class MatchResultRepositoryImpl @Inject constructor(
    coroutineScope: CoroutineScope,
    database: PluginDatabase
): MatchResultsRepository {

    private val matchResultsDao = database.getMatchResultsDao()
    private val cache = Collections.synchronizedList(mutableListOf<MatchResult>())

    init {
        coroutineScope.launch { cache.addAll(matchResultsDao.getAll()) }
    }

    override fun getCachedResults(): List<MatchResult> = cache.toList()

    override suspend fun addResult(winnerFractionId: Long, looserFractionId: Long) {
        cache.add(MatchResult(winnerFractionId, looserFractionId))
        matchResultsDao.add(winnerFractionId, looserFractionId)
    }

    override suspend fun addDrawResult() {
        cache.add(MatchResult(null, null))
        matchResultsDao.addDrawResult()
    }

}