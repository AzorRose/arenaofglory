package ru.mainmayhem.arenaofglory.data.local.database

import ru.mainmayhem.arenaofglory.data.local.database.dao.*

interface PluginDatabase {

    fun getFractionDao(): FractionDao

    fun getArenaPlayersDao(): ArenaPlayersDao

    fun getWaitingRoomCoordinatesDao(): WaitingRoomCoordinatesDao

    fun getArenaRespawnCoordinatesDao(): ArenaRespawnCoordinatesDao

    fun getRewardDao(): RewardDao

    fun getArenaCoordinatesDao(): ArenaCoordinatesDao

    fun close()

}