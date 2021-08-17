package ru.mainmayhem.arenaofglory.data.local.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaPlayersDao
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaPlayers
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Fractions

class JetbrainsExposedDatabase(
    private val database: Database,
    private val fractionDao: FractionDao,
    private val playersDao: ArenaPlayersDao
): PluginDatabase {

    init {
        createTables()
    }

    override fun getFractionDao(): FractionDao = fractionDao

    override fun getArenaPlayersDao(): ArenaPlayersDao = playersDao

    override fun close() {}

    private fun createTables(){
        SchemaUtils.create(Fractions)
        SchemaUtils.create(ArenaPlayers)
    }

}