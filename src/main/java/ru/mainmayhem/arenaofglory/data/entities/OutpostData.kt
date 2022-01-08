package ru.mainmayhem.arenaofglory.data.entities

import ru.mainmayhem.arenaofglory.domain.CalculatedLocation

data class OutpostData(
    val outpost: Outpost,
    val calculatedLocation: CalculatedLocation
)
