package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.WaitingRoomCoordinates

class JEWaitingRoomCoordinatesDao(
    private val dispatchers: CoroutineDispatchers
): WaitingRoomCoordinatesDao {

    private val topLeftCorner = "top_left_corner"
    private val bottomRightCorner = "bottom_right_corner"

    private var stateFlow: MutableStateFlow<LocationCoordinates>? = null

    override suspend fun insert(coordinates: LocationCoordinates) {
        withContext(dispatchers.io){
            transaction {
                WaitingRoomCoordinates.insert {
                    it[type] = topLeftCorner
                    it[x] = coordinates.leftTop.x
                    it[y] = coordinates.leftTop.y
                    it[z] = coordinates.leftTop.z
                }
                WaitingRoomCoordinates.insert {
                    it[type] = bottomRightCorner
                    it[x] = coordinates.rightBottom.x
                    it[y] = coordinates.rightBottom.y
                    it[z] = coordinates.rightBottom.z
                }
            }
        }
    }

    override suspend fun get(): LocationCoordinates {
        return withContext(dispatchers.io){
            transaction {
                val topLeft = WaitingRoomCoordinates.select(
                    Op.build {
                        WaitingRoomCoordinates.type eq topLeftCorner
                    }
                ).first().toModel()
                val bottomRight = WaitingRoomCoordinates.select(
                    Op.build {
                        WaitingRoomCoordinates.type eq bottomRightCorner
                    }
                ).first().toModel()
                LocationCoordinates(
                    leftTop = topLeft,
                    rightBottom = bottomRight
                )
            }
        }
    }

    override suspend fun locationFlow(): Flow<LocationCoordinates> {
        return stateFlow ?: MutableStateFlow(get()).also {
            stateFlow = it
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(dispatchers.io){
            transaction {
                WaitingRoomCoordinates.selectAll().empty()
            }
        }
    }

    private fun ResultRow.toModel(): Coordinates{
        return Coordinates(
            x = get(WaitingRoomCoordinates.x),
            y = get(WaitingRoomCoordinates.y),
            z = get(WaitingRoomCoordinates.z)
        )
    }

}