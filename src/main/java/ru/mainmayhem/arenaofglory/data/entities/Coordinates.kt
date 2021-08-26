package ru.mainmayhem.arenaofglory.data.entities

import org.bukkit.Location
import org.bukkit.World

data class Coordinates(
    //возрастает с запада на восток
    val x: Int,
    //возрастает снизу вверх
    val y: Int,
    //возрастает с севера на юг
    val z: Int
){

    fun getLocation(world: World?): Location{
        return Location(
            world,
            x.toDouble(),
            y.toDouble(),
            z.toDouble()
        )
    }

}
