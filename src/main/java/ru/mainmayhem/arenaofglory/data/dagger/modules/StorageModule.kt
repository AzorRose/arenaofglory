package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.local.database.JetbrainsExposedDatabase
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.database.dao.*
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.*
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    @Singleton
    fun getFractionDao(d: CoroutineDispatchers): FractionDao = JetbrainsExposedFractionDao(d)

    @Provides
    @Singleton
    fun getArenaPlayersDao(
        d: CoroutineDispatchers
    ): ArenaPlayersDao = JetbrainsExposedArenaPlayersDao(d)

    @Provides
    @Singleton
    fun getWaitingRoomCoordinatesDao(
        d: CoroutineDispatchers
    ): WaitingRoomCoordinatesDao = JEWaitingRoomCoordinatesDao(d)

    @Provides
    @Singleton
    fun getArenaRespawnCoordinatesDao(
        d: CoroutineDispatchers
    ): ArenaRespawnCoordinatesDao = JEArenaRespawnCoordinatesDao(d)

    @Provides
    @Singleton
    fun getRewardDao(
        d: CoroutineDispatchers
    ): RewardDao = JERewardDao(d)

    @Provides
    @Singleton
    fun getDatabase(
        fd: FractionDao,
        apd: ArenaPlayersDao,
        wrcd: WaitingRoomCoordinatesDao,
        arcd: ArenaRespawnCoordinatesDao,
        rewardDao: RewardDao,
        dbCfgRep: DbConfigFileRepository,
        logger: PluginLogger
    ): PluginDatabase =
        JetbrainsExposedDatabase(
            fractionDao = fd,
            playersDao = apd,
            waitingRoomCoordinatesDao = wrcd,
            arenaRespawnCoordinatesDao = arcd,
            rewardDao = rewardDao,
            dbConfigRepository = dbCfgRep,
            logger = logger
        )

}