package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

/**
 * При создании подписывается на изменение таблицы arena_players
 * Результат кэширует
 */
interface ArenaPlayersRepository {

    fun getCachedPlayers(): List<ArenaPlayer>

    fun getCachedPlayerById(playerId: String): ArenaPlayer?

    fun getCachedPlayerByName(playerName: String): ArenaPlayer?

}