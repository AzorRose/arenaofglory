package ru.mainmayhem.arenaofglory.domain

import javax.inject.Inject
import kotlinx.coroutines.withContext
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates

class CoordinatesCalculator @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) {

    /**
     * Принимает две точки локации и отдает все точки, принадлежащие данной локации
     * Точки округляются до целых числ
     * Отдает только X, Z. Y ВСЕГДА равен значению верхнего левого угла
     */
    suspend fun calculate(locationCoordinates: LocationCoordinates): CalculatedLocation {
        return withContext(dispatchers.io) {
            doCalculation(locationCoordinates)
        }
    }

    private fun doCalculation(locationCoordinates: LocationCoordinates): CalculatedLocation {
        val topLeft = locationCoordinates.leftTop
        val bottomRight = locationCoordinates.rightBottom
        val coordinates = mutableListOf<Coordinates>()
        val minX = if (topLeft.x < bottomRight.x) topLeft.x else bottomRight.x
        val maxX = if (topLeft.x > bottomRight.x) topLeft.x else bottomRight.x
        val minZ = if (topLeft.z < bottomRight.z) topLeft.z else bottomRight.z
        val maxZ = if (topLeft.z > bottomRight.z) topLeft.z else bottomRight.z
        getProgression(topLeft.z, bottomRight.z).forEach { z ->
            getProgression(topLeft.x, bottomRight.x).forEach { x ->
                coordinates.add(Coordinates(x, topLeft.y, z))
            }
        }
        return CalculatedLocation(minX, maxX, minZ, maxZ, coordinates)
    }

    private fun getProgression(first: Int, second: Int): IntProgression {
        val doDownLoop = first > second
        return if (doDownLoop) first downTo second else first..second
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