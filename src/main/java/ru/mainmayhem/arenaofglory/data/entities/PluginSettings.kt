package ru.mainmayhem.arenaofglory.data.entities

import java.util.*

data class PluginSettings(
    val openWaitingRoomMins: Int,
    //только время, без даты!
    val startArenaMatch: List<Date>,
    val minKillsForReward: Int,
    //в минутах
    val matchDuration: Int
)