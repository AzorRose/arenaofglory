package ru.mainmayhem.arenaofglory.domain

import kotlinx.coroutines.withContext
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import javax.inject.Inject

class CoordinatesCalculator @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) {

    /**
     * Принимает две точки локации и отдает все точки, принадлежащие данной локации
     * Точки округляются до целых числ
     * Отдает только X, Z. Y ВСЕГДА равен 0
     */
    suspend fun calculate(locationCoordinates: LocationCoordinates): CalculatedLocation{
        return withContext(dispatchers.io){
            doCalculation(locationCoordinates)
        }
    }

    private fun doCalculation(locationCoordinates: LocationCoordinates): CalculatedLocation{
        val topLeft = locationCoordinates.leftTop
        val bottomRight = locationCoordinates.rightBottom
        val coordinates = mutableListOf<Coordinates>()
        var minX = 0
        var maxX = 0
        var minZ = 0
        var maxZ = 0
        (topLeft.z..bottomRight.z).forEach { z ->
            if (minZ > z)
                minZ = z
            if (z > maxZ)
                maxZ = z
            (topLeft.x..bottomRight.x).forEach { x ->
                if (minX > x)
                    minX = x
                if (x > maxX)
                    maxX = x
                coordinates.add(Coordinates(x, 0, z))
            }
        }
        return CalculatedLocation(minX, maxX, minZ, maxZ, coordinates)
    }

}

/**
 * Некая область на карте и все ее координаты, которые она занимает
 */
data class CalculatedLocation(
    val minX: Int,
    val maxX: Int,
    val minZ: Int,
    val maxZ: Int,
    val coordinates: List<Coordinates>
)