package ru.mainmayhem.arenaofglory.data.entities

data class Coordinates(
    //возрастает с запада на восток
    val x: Int,
    //возрастает снизу вверх
    val y: Int,
    //возрастает с севера на юг
    val z: Int
)
