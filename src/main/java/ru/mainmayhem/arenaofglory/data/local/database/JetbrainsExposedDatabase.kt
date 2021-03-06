package ru.mainmayhem.arenaofglory.data.local.database

import java.sql.Connection
import javax.inject.Inject
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaRespawnCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.MatchResultsDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.OutpostsDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.RewardDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaPlayers
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaRespawnCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Fractions
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.MatchResults
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Outposts
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Reward
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.WaitingRoomCoordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

class JetbrainsExposedDatabase @Inject constructor(
    private val fractionDao: FractionDao,
    private val playersDao: ArenaPlayersDao,
    private val waitingRoomCoordinatesDao: WaitingRoomCoordinatesDao,
    private val arenaRespawnCoordinatesDao: ArenaRespawnCoordinatesDao,
    private val rewardDao: RewardDao,
    private val arenaCoordsDao: ArenaCoordinatesDao,
    private val matchResDao: MatchResultsDao,
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

    override fun getMatchResultsDao(): MatchResultsDao = matchResDao

    override fun getOutpostsDao(): OutpostsDao = dbOutpostsDao

    override fun close() { /*empty*/ }

    private fun createTables() {
        transaction {
            SchemaUtils.create(
                Fractions,
                ArenaPlayers,
                WaitingRoomCoordinates,
                ArenaRespawnCoordinates,
                Reward,
                ArenaCoordinates,
                Outposts,
                MatchResults
            )
            SchemaUtils.createMissingTablesAndColumns(
                ArenaPlayers, Outposts, MatchResults
            )
        }
    }

    private fun connectToDatabase() {
        val config = dbConfigRepository.getConfigFromFile()
        logger.info("?????????????????????? ?? ???? ?? ??????????????????????????: $config")
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