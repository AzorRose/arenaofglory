package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object ArenaRespawnCoordinates: Table() {
    val fractionId = long("fraction_id").references(Fractions.id)
    val topLeftCornerX = WaitingRoomCoordinates.integer("top_left_corner_x")
    val topLeftCornerY = WaitingRoomCoordinates.integer("top_left_corner_y")
    val topLeftCornerZ = WaitingRoomCoordinates.integer("top_left_corner_z")
    val bottomRightCornerX = WaitingRoomCoordinates.integer("bottom_right_corner_x")
    val bottomRightCornerY = WaitingRoomCoordinates.integer("bottom_right_corner_y")
    val bottomRightCornerZ = WaitingRoomCoordinates.integer("bottom_right_corner_z")
    override val tableName: String = "arena_respawn_coordinates"
    override val primaryKey: Table.PrimaryKey = PrimaryKey(fractionId)
}