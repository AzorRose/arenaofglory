package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import org.jetbrains.exposed.sql.Database
import ru.mainmayhem.arenaofglory.data.local.database.JetbrainsExposedDatabase
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.exposed.JetbrainsExposedFractionDao
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.impls.DbConfigFileRepoImpl
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    fun getDbConfig(logger: PluginLogger): DbConfigFileRepository = DbConfigFileRepoImpl(logger)

    @Provides
    @Singleton
    fun getJetbrainsExposedDatabase(dbConfigRepository: DbConfigFileRepository): Database{
        val config = dbConfigRepository.getConfigFromFile()
        return Database.connect(
            url = config.url,
            driver = config.driver,
            user = config.user.orEmpty(),
            password = config.password.orEmpty()
        )
    }

    @Provides
    @Singleton
    fun getFractionDao(): FractionDao = JetbrainsExposedFractionDao()

    @Provides
    @Singleton
    fun getDatabase(db: Database, fd: FractionDao): PluginDatabase =
        JetbrainsExposedDatabase(
            database = db,
            fractionDao = fd
        )

}