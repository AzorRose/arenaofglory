package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object ArenaRespawnCoordinates: Table() {
    val fractionId = long("fraction_id").references(Fractions.id)
    val topLeftCornerX = integer("top_left_corner_x")
    val topLeftCornerY = integer("top_left_corner_y")
    val topLeftCornerZ = integer("top_left_corner_z")
    val bottomRightCornerX = integer("bottom_right_corner_x")
    val bottomRightCornerY = integer("bottom_right_corner_y")
    val bottomRightCornerZ = integer("bottom_right_corner_z")
    override val tableName: String = "arena_respawn_coordinates"
    override val primaryKey: Table.PrimaryKey = PrimaryKey(fractionId)
}