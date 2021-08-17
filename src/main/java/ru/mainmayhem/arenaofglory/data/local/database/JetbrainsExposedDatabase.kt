package ru.mainmayhem.arenaofglory.data.local.database

import org.jetbrains.exposed.sql.Database
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao

class JetbrainsExposedDatabase(
    private val database: Database,
    private val fractionDao: FractionDao
): PluginDatabase {

    override fun getFractionDao(): FractionDao = fractionDao

    override fun close() {

    }

}