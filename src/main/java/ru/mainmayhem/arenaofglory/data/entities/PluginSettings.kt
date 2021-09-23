package ru.mainmayhem.arenaofglory.data.entities

import java.util.*

data class PluginSettings(
    val openWaitingRoom: Date,
    val startArenaMatch: Date,
    val minKillsForReward: Int,
    //в минутах
    val matchDuration: Int,
    val outpostConqueringDuration: Int
)