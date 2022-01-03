package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import ru.mainmayhem.arenaofglory.data.local.repositories.*
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.*
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun getDbConfigRepository(impl: DbConfigFileRepoImpl): DbConfigFileRepository

    @Binds
    @Singleton
    abstract fun getArenaPlayersRepository(impl: ArenaPlayersRepositoryImpl): ArenaPlayersRepository

    @Binds
    @Singleton
    abstract fun getFractionsRepository(impl: FractionsRepositoryImpl): FractionsRepository

    @Binds
    @Singleton
    abstract fun getWRCoordinatesRepository(impl: WRCoordinatesRepositoryImpl): WaitingRoomCoordinatesRepository

    @Binds
    @Singleton
    abstract fun getArenaQueueRepository(impl: ArenaQueueRepositoryImpl): ArenaQueueRepository

    @Binds
    @Singleton
    abstract fun getArenaMatchMetaRepository(impl: ArenaMatchMetaRepositoryImpl): ArenaMatchMetaRepository

    @Binds
    @Singleton
    abstract fun getArenaRespawnCoordinatesRepository(impl: ArenaRespawnCoordinatesRepositoryImpl): ArenaRespawnCoordinatesRepository

    @Binds
    @Singleton
    abstract fun getRewardRepository(impl: RewardRepositoryImpl): RewardRepository

    @Binds
    @Singleton
    abstract fun getSettingsRepository(impl: SettingsRepositoryImpl): PluginSettingsRepository

    @Binds
    @Singleton
    abstract fun getArenaCoordinatesRepository(impl: ArenaCoordinatesRepositoryImpl): ArenaCoordinatesRepository

    @Binds
    @Singleton
    abstract fun getOutpostsRepository(impl: OutpostsRepositoryImpl): OutpostsRepository

    @Provides
    @Singleton
    fun getMatchResultsRepository(
        coroutineScope: CoroutineScope,
        database: PluginDatabase
    ): MatchResultsRepository = MatchResultRepositoryImpl(coroutineScope, database)

}