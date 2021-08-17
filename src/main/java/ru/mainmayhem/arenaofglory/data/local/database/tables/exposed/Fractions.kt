package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object Fractions: Table() {
    val id = long("id")
    val name = varchar("name", 25)
    val motto = varchar("motto", 100)
    override val tableName: String = "fractions"
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}