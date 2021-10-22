package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import java.util.*
import javax.inject.Inject

class ArenaPlayersRepositoryImpl @Inject constructor(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope
): ArenaPlayersRepository {

    private val players = Collections.synchronizedList(mutableListOf<ArenaPlayer>())

    init {
        coroutineScope.launch {
            pluginDatabase.getArenaPlayersDao()
                .getPlayersFlow()
                .collectLatest {
                    players.clear()
                    players.addAll(it)
                }
        }
    }

    override fun getCachedPlayers(): List<ArenaPlayer> = players

    override fun getCachedPlayerById(playerId: String): ArenaPlayer? {
        return players.find { it.id == playerId }
    }

    override fun getCachedPlayerByName(playerName: String): ArenaPlayer? {
        return players.find { it.name == playerName }
    }

}