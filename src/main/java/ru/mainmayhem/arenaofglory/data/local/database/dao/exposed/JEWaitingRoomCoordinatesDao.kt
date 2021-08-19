package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.WaitingRoomCoordinates

class JEWaitingRoomCoordinatesDao(
    private val dispatchers: CoroutineDispatchers
): WaitingRoomCoordinatesDao {

    private val topLeftCorner = "top_left_corner"
    private val topBottomRightCorner = "top_left_corner"

    override suspend fun insert(coordinates: LocationCoordinates) {
        withContext(dispatchers.io){
            WaitingRoomCoordinates.insert {
                it[WaitingRoomCoordinates.type = ]
            }
        }
    }

    override suspend fun get(): LocationCoordinates {
        TODO("Not yet implemented")
    }

}