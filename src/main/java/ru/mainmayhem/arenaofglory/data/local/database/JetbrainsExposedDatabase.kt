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


class JetbrainsExposedDatabase(
    private val fractionDao: FractionDao,
    private val playersDao: ArenaPlayersDao,
    private val waitingRoomCoordinatesDao: WaitingRoomCoordinatesDao,
    private val arenaRespawnCoordinatesDao: ArenaRespawnCoordinatesDao,
    private val rewardDao: RewardDao,
    private val arenaCoordsDao: ArenaCoordinatesDao,
    private val matchResDao: MatchResultsDao,
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

    override fun getArenaRespawnCoordinatesDao(): ArenaRespawnCoordinatesDao = arenaRespawnCoordinatesDao

    override fun getRewardDao(): RewardDao = rewardDao

    override fun getArenaCoordinatesDao(): ArenaCoordinatesDao = arenaCoordsDao

    override fun getMatchResultsDao(): MatchResultsDao = matchResDao

    override fun close() {}

    private fun createTables(){
        transaction {
            SchemaUtils.create(
                Fractions,
                ArenaPlayers,
                WaitingRoomCoordinates,
                ArenaRespawnCoordinates,
                Reward,
                ArenaCoordinates
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