package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object ArenaCoordinates: Table() {
    val type = varchar("id", 25)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    override val tableName: String = "arena_coordinates"
    override val primaryKey: Table.PrimaryKey = PrimaryKey(type)
}