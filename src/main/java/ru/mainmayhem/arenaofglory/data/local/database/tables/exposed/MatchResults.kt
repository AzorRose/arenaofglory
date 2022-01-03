package ru.mainmayhem.arenaofglory.data.local.database.tables.exposed

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object MatchResults: Table() {
    val id = long("id").autoIncrement()
    val winnerFractionId = long("winner_fraction_id").nullable()
    val looserFractionId = long("looser_fraction_id").nullable()
    val date = datetime("date").defaultExpression(CurrentDateTime())
    override val tableName: String = "match_results"
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}