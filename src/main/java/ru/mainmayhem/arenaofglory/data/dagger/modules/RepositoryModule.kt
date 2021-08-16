package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.DbConfigFileRepoImpl
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

@Module
class RepositoryModule {

    @Provides
    fun getDbConfigRepository(logger: PluginLogger): DbConfigFileRepository = DbConfigFileRepoImpl(logger)

}