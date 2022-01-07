package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import ru.mainmayhem.arenaofglory.data.local.database.JetbrainsExposedDatabase
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaRespawnCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.MatchResultsDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.OutpostsDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.RewardDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JEArenaCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JEArenaRespawnCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JEMatchResultsDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JERewardDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JEWaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JetbrainsExposedArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JetbrainsExposedFractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JetbrainsExposedOutpostsDao

@Module
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun getFractionDao(impl: JetbrainsExposedFractionDao): FractionDao

    @Binds
    @Singleton
    abstract fun getArenaPlayersDao(impl: JetbrainsExposedArenaPlayersDao): ArenaPlayersDao

    @Binds
    @Singleton
    abstract fun getWaitingRoomCoordinatesDao(impl: JEWaitingRoomCoordinatesDao): WaitingRoomCoordinatesDao

    @Binds
    @Singleton
    abstract fun getArenaRespawnCoordinatesDao(impl: JEArenaRespawnCoordinatesDao): ArenaRespawnCoordinatesDao

    @Binds
    @Singleton
    abstract fun getRewardDao(impl: JERewardDao): RewardDao

    @Binds
    @Singleton
    abstract fun getArenaCoordinatesDao(impl: JEArenaCoordinatesDao): ArenaCoordinatesDao

    @Binds
    @Singleton
    abstract fun getOutpostsDao(impl: JetbrainsExposedOutpostsDao): OutpostsDao

    @Binds
    abstract fun getMatchResultsDao(impl: JEMatchResultsDao): MatchResultsDao

    @Binds
    @Singleton
    abstract fun getDatabase(db: JetbrainsExposedDatabase): PluginDatabase

}