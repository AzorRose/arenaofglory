package ru.mainmayhem.arenaofglory.data.local.database

import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao

interface PluginDatabase {

    fun getFractionDao(): FractionDao

    fun getArenaPlayersDao(): ArenaPlayersDao

    fun getWaitingRoomCoordinatesDao(): WaitingRoomCoordinatesDao

    fun close()

}