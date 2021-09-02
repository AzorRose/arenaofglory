package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object WaitingRoomCoordinates: Table() {
    val type = varchar("id", 25)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    override val tableName: String = "waiting_room_coordinates"
    override val primaryKey: PrimaryKey = PrimaryKey(type)
}