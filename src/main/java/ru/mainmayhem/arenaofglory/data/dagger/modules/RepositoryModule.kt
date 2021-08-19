package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.ArenaPlayersRepositoryImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.DbConfigFileRepoImpl
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.FractionsRepositoryImpl
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun getDbConfigRepository(logger: PluginLogger): DbConfigFileRepository = DbConfigFileRepoImpl(logger)

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

}