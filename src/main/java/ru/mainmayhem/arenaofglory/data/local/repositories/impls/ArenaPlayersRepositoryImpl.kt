package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository

class ArenaPlayersRepositoryImpl(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers
): ArenaPlayersRepository {

    private var players = emptyList<ArenaPlayer>()

    init {
        coroutineScope.launch(dispatchers.main) {
            pluginDatabase.getArenaPlayersDao()
                .getPlayersFlow()
                .collectLatest {
                    players = it
                }
        }
    }

    override fun getCachedPlayers(): List<ArenaPlayer> = players

    override fun getCachedPlayerById(playerId: String): ArenaPlayer? {
        return players.find { it.id == playerId }
    }

}