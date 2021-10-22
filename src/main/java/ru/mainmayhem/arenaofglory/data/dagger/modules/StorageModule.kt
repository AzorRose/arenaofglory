package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import ru.mainmayhem.arenaofglory.data.local.database.JetbrainsExposedDatabase
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.database.dao.*
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.*
import javax.inject.Singleton

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
    @Singleton
    abstract fun getDatabase(db: JetbrainsExposedDatabase): PluginDatabase

}