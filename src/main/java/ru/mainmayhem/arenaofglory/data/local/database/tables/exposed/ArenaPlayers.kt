package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object ArenaPlayers: Table() {
    val id = varchar("id", 36)
    val fractionId = long("fraction_id").references(Fractions.id)
    val name = varchar("name", 25)
    override val tableName: String = "arena_players"
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}