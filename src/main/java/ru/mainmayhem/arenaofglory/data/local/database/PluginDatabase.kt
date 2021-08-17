package ru.mainmayhem.arenaofglory.data.local.database

import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao

interface PluginDatabase {

    fun getFractionDao(): FractionDao

    fun getArenaPlayersDao(): ArenaPlayersDao

    fun close()

}