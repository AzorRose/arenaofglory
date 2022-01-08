package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository

class ArenaPlayersRepositoryImpl @Inject constructor(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope
): ArenaPlayersRepository {

    private val playersByName = Collections.synchronizedMap(mutableMapOf<String, ArenaPlayer>())
    private val playersById = Collections.synchronizedMap(mutableMapOf<String, ArenaPlayer>())

    init {
        coroutineScope.launch {
            pluginDatabase.getArenaPlayersDao()
                .getPlayersFlow()
                .collectLatest { playersFromDb ->
                    playersFromDb.forEach { player ->
                        playersByName[player.name] = player
                        playersById[player.id] = player
                    }
                }
        }
    }

    override fun getCachedPlayers(): List<ArenaPlayer> = playersByName.values.toList()

    override fun getCachedPlayerById(playerId: String): ArenaPlayer? {
        return playersById[playerId]
    }

    override fun getCachedPlayerByName(playerName: String): ArenaPlayer? {
        return playersByName[playerName]
    }

}