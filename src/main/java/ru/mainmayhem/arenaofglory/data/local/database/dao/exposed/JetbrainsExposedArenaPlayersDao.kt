package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaPlayers

class JetbrainsExposedArenaPlayersDao(
    private val dispatchers: CoroutineDispatchers
): ArenaPlayersDao {

    private var stateFlow: MutableStateFlow<List<ArenaPlayer>>? = null

    override suspend fun insert(player: ArenaPlayer) {
        return withContext(dispatchers.io){
            transaction {
                ArenaPlayers.insert {
                    it[id] = player.id
                    it[name] = player.name
                    it[fractionId] = player.fractionId
                }
            }
            stateFlow?.value = getAll()
        }
    }

    override suspend fun update(player: ArenaPlayer) {
        return withContext(dispatchers.io){
            transaction {
                ArenaPlayers.update {
                    it[id] = player.id
                    it[name] = player.name
                    it[fractionId] = player.fractionId
                }
            }
            stateFlow?.value = getAll()
        }
    }

    override suspend fun getByPlayerId(playerId: String): ArenaPlayer? {
        return withContext(dispatchers.io){
            transaction {
                ArenaPlayers.select { ArenaPlayers.id eq playerId }.firstOrNull()?.toModel()
            }
        }
    }

    override suspend fun getByFractionId(fractionId: Long): List<ArenaPlayer> {
        return withContext(dispatchers.io){
            transaction {
                ArenaPlayers.select { ArenaPlayers.fractionId eq fractionId }.toList().map { it.toModel() }
            }
        }
    }

    override suspend fun getAll(): List<ArenaPlayer> {
        return withContext(dispatchers.io){
            transaction {
                ArenaPlayers.selectAll().toList().map { it.toModel() }
            }
        }
    }

    override suspend fun getPlayersFlow(): Flow<List<ArenaPlayer>>{
        return stateFlow ?: MutableStateFlow(getAll()).also {
            stateFlow = it
        }
    }

    private fun ResultRow.toModel(): ArenaPlayer{
        return ArenaPlayer(
            id = get(ArenaPlayers.id),
            name = get(ArenaPlayers.name),
            fractionId = get(ArenaPlayers.fractionId)
        )
    }

}