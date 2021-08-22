package ru.mainmayhem.arenaofglory.data.local.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaPlayers
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaRespawnCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Fractions
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.WaitingRoomCoordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.sql.Connection

class JetbrainsExposedDatabase(
    private val fractionDao: FractionDao,
    private val playersDao: ArenaPlayersDao,
    private val waitingRoomCoordinatesDao: WaitingRoomCoordinatesDao,
    private val dbConfigRepository: DbConfigFileRepository,
    private val logger: PluginLogger
): PluginDatabase {

    init {
        connectToDatabase()
        createTables()
    }

    override fun getFractionDao(): FractionDao = fractionDao

    override fun getArenaPlayersDao(): ArenaPlayersDao = playersDao

    override fun getWaitingRoomCoordinatesDao(): WaitingRoomCoordinatesDao = waitingRoomCoordinatesDao

    override fun close() {}

    private fun createTables(){
        transaction {
            SchemaUtils.create(
                Fractions,
                ArenaPlayers,
                WaitingRoomCoordinates,
                ArenaRespawnCoordinates
            )
        }
    }

    private fun connectToDatabase(){
        val config = dbConfigRepository.getConfigFromFile()
        logger.info("Подключение к БД с конфигурацией: $config")
        Database.connect(
            url = config.url,
            driver = config.driver,
            user = config.user,
            password = config.password
        )
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
    }

}