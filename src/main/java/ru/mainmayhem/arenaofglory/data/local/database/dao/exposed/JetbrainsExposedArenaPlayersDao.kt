package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao

class JetbrainsExposedArenaPlayersDao(
    private val dispatchers: CoroutineDispatchers
): ArenaPlayersDao {

    override suspend fun insert(player: ArenaPlayer) {
        TODO("Not yet implemented")
    }

    override suspend fun getByPlayerId(playerId: String): ArenaPlayer {
        TODO("Not yet implemented")
    }

    override suspend fun getByFractionId(fractionId: Long): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }

}