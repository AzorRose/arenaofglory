package ru.mainmayhem.arenaofglory.data.entities

/**
 * Координаты какой-то локации аля комната ожидания, респавн или арена
 */
data class LocationCoordinates(
    val leftTop: Coordinates,
    val rightBottom: Coordinates
)