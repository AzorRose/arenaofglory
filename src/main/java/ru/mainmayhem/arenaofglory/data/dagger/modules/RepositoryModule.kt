package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.MatchResultsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.RewardRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaCoordinatesRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaMatchMetaRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaPlayersRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaQueueRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaRespawnCoordinatesRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.DbConfigFileRepoImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.FractionsRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.MatchResultRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.OutpostsRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.RewardRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.SettingsRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.WRCoordinatesRepositoryImpl
import ru.mainmayhem.arenaofglory.resources.files.BuyerPluginDirectoryRepository
import ru.mainmayhem.arenaofglory.resources.files.PluginDirectoryRepository
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

    @Binds
    @Singleton
    abstract fun getMatchResultsRepository(impl: MatchResultRepositoryImpl): MatchResultsRepository

    @Binds
    abstract fun bindPluginDirectoryRepository(impl: BuyerPluginDirectoryRepository): PluginDirectoryRepository
}