package ru.mainmayhem.arenaofglory.data.entities

import java.util.Date

data class PluginSettings(
    val openWaitingRoomMins: Int,
    //только время, без даты!
    val startArenaMatch: List<Date>,
    val minKillsForReward: Int,
    //в минутах
    val matchDuration: Int,
    val fractionBoostDefeats: Int,
    val outpostConqueringDuration: Int
)