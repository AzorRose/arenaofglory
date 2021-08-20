package ru.mainmayhem.arenaofglory.domain

import ru.mainmayhem.arenaofglory.data.entities.Coordinates

class CoordinatesComparator{

    /**
     * Метод, который проверяет, попадает ли переданная точка в некую область на карте
     * ПРОВЕРЯЕТ ТОЛЬКО ПО Х И Z
     * @param coordinates - координата точки
     * @param location - локация на карте
     * @return true - точка находится внутри этой локации
     */
    fun compare(coordinates: Coordinates, location: CalculatedLocation): Boolean{
        val x = coordinates.x
        val z = coordinates.z
        val minX = location.minX
        val maxX = location.maxX
        val minZ = location.minZ
        val maxZ = location.maxZ

        return x in minX..maxX && z in minZ..maxZ

    }

}