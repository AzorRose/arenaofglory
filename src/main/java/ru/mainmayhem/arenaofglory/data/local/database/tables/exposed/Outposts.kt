package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object Outposts: Table() {
    val id = long("id")
    val name = varchar("name", 25)
    val fractionId = long("fraction_id").nullable()
    val topLeftCornerX = integer("top_left_corner_x")
    val topLeftCornerY = integer("top_left_corner_y")
    val topLeftCornerZ = integer("top_left_corner_z")
    val bottomRightCornerX = integer("bottom_right_corner_x")
    val bottomRightCornerY = integer("bottom_right_corner_y")
    val bottomRightCornerZ = integer("bottom_right_corner_z")
    val rewardCommands = varchar("reward_commands", 500).nullable()
    override val tableName: String = "outposts"
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}