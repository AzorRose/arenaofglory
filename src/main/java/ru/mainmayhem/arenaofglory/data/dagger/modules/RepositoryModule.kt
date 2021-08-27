package ru.mainmayhem.arenaofglory.data.dagger.modules

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.*
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.*
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun getDbConfigRepository(logger: PluginLogger, m: Moshi): DbConfigFileRepository = DbConfigFileRepoImpl(logger, m)

    @Provides
    @Singleton
    fun getArenaPlayersRepository(
        db: PluginDatabase,
        cs: CoroutineScope,
    ): ArenaPlayersRepository = ArenaPlayersRepositoryImpl(
        pluginDatabase = db,
        coroutineScope = cs
    )

    @Provides
    @Singleton
    fun getFractionsRepository(
        db: PluginDatabase,
        cs: CoroutineScope
    ): FractionsRepository = FractionsRepositoryImpl(
        pluginDatabase = db,
        coroutineScope = cs
    )

    @Provides
    @Singleton
    fun getWRCoordinatesRepository(
        calculator: CoordinatesCalculator,
        coroutineScope: CoroutineScope,
        database: PluginDatabase
    ): WaitingRoomCoordinatesRepository = WRCoordinatesRepositoryImpl(calculator, database, coroutineScope)

    @Provides
    @Singleton
    fun getArenaQueueRepository(
        ar: ArenaPlayersRepository
    ): ArenaQueueRepository = ArenaQueueRepositoryImpl(ar)

    @Provides
    @Singleton
    fun getArenaMatchMetaRepository(
       l: PluginLogger,
       fr: FractionsRepository
    ): ArenaMatchMetaRepository = ArenaMatchMetaRepositoryImpl(l, fr)

    @Provides
    @Singleton
    fun getArenaRespawnCoordinatesRepository(
        calculator: CoordinatesCalculator,
        coroutineScope: CoroutineScope,
        database: PluginDatabase
    ): ArenaRespawnCoordinatesRepository =
        ArenaRespawnCoordinatesRepositoryImpl(calculator, database, coroutineScope)

}