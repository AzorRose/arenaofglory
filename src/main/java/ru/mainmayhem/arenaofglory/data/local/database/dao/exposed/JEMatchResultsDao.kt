package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.MatchResult
import ru.mainmayhem.arenaofglory.data.local.database.dao.MatchResultsDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.MatchResults

class JEMatchResultsDao @Inject constructor(
    private val dispatchers: CoroutineDispatchers
): MatchResultsDao {

    override suspend fun getAll(): List<MatchResult> {
        return withContext(dispatchers.io) {
            transaction {
                MatchResults.selectAll().toList().map { row -> row.toModel() }
            }
        }
    }

    override suspend fun add(winnerId: Long, looserId: Long) {
        withContext(dispatchers.io) {
            transaction {
                MatchResults.insert {
                    it[winnerFractionId] = winnerId
                    it[looserFractionId] = looserId
                }
            }
        }
    }

    override suspend fun addDrawResult() {
        withContext(dispatchers.io) {
            transaction {
                MatchResults.insert {
                    it[winnerFractionId] = null
                    it[looserFractionId] = null
                }
            }
        }
    }

    private fun ResultRow.toModel(): MatchResult {
        return MatchResult(
            winnerFractionId = get(MatchResults.winnerFractionId),
            looserFractionId = get(MatchResults.looserFractionId)
        )
    }

}