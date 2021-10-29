package ru.mainmayhem.arenaofglory.data.entities

data class Outpost(
    val id: Long,
    val name: String,
    val fractionId: Long?,
    val coordinates: LocationCoordinates,
    val rewardCommands: List<Command>
)