package ru.mainmayhem.arenaofglory.data.local.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.local.database.dao.*
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.*
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.sql.Connection
import javax.inject.Inject


class JetbrainsExposedDatabase @Inject constructor(
    private val fractionDao: FractionDao,
    private val playersDao: ArenaPlayersDao,
    private val waitingRoomCoordinatesDao: WaitingRoomCoordinatesDao,
    private val arenaRespawnCoordinatesDao: ArenaRespawnCoordinatesDao,
    private val rewardDao: RewardDao,
    private val arenaCoordsDao: ArenaCoordinatesDao,
    private val dbConfigRepository: DbConfigFileRepository,
    private val dbOutpostsDao: OutpostsDao,
    private val logger: PluginLogger
): PluginDatabase {

    init {
        connectToDatabase()
        createTables()
    }

    override fun getFractionDao(): FractionDao = fractionDao

    override fun getArenaPlayersDao(): ArenaPlayersDao = playersDao

    override fun getWaitingRoomCoordinatesDao(): WaitingRoomCoordinatesDao = waitingRoomCoordinatesDao

    override fun getArenaRespawnCoordinatesDao(): ArenaRespawnCoordinatesDao = arenaRespawnCoordinatesDao

    override fun getRewardDao(): RewardDao = rewardDao

    override fun getArenaCoordinatesDao(): ArenaCoordinatesDao = arenaCoordsDao

    override fun getOutpostsDao(): OutpostsDao = dbOutpostsDao

    override fun close() {}

    private fun createTables(){
        transaction {
            SchemaUtils.create(
                Fractions,
                ArenaPlayers,
                WaitingRoomCoordinates,
                ArenaRespawnCoordinates,
                Reward,
                ArenaCoordinates,
                Outposts
            )
            SchemaUtils.createMissingTablesAndColumns(
                ArenaPlayers
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