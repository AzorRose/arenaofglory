package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table

object Reward: Table() {
    val type = varchar("type", 10)
    val tokensAmount = integer("tokens_amount")
    override val tableName: String = "reward"
    override val primaryKey: PrimaryKey = PrimaryKey(type)
}