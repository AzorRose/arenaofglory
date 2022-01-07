package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.dao.WaitingRoomCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.WaitingRoomCoordinates

private const val TOP_LEFT_CORNER = "top_left_corner"
private const val BOTTOM_RIGHT_CORNER = "bottom_right_corner"

class JEWaitingRoomCoordinatesDao @Inject constructor(
    private val dispatchers: CoroutineDispatchers
): WaitingRoomCoordinatesDao {

    private var stateFlow: MutableStateFlow<LocationCoordinates?>? = null

    override suspend fun insert(coordinates: LocationCoordinates) {
        withContext(dispatchers.io) {
            transaction {
                WaitingRoomCoordinates.insert {
                    it[type] = TOP_LEFT_CORNER
                    it[x] = coordinates.leftTop.x
                    it[y] = coordinates.leftTop.y
                    it[z] = coordinates.leftTop.z
                }
                WaitingRoomCoordinates.insert {
                    it[type] = BOTTOM_RIGHT_CORNER
                    it[x] = coordinates.rightBottom.x
                    it[y] = coordinates.rightBottom.y
                    it[z] = coordinates.rightBottom.z
                }
            }
        }
    }

    override suspend fun get(): LocationCoordinates? {
        return withContext(dispatchers.io) {
            transaction {
                val topLeft = WaitingRoomCoordinates.select(
                    Op.build {
                        WaitingRoomCoordinates.type eq TOP_LEFT_CORNER
                    }
                ).firstOrNull()?.toModel() ?: return@transaction null
                val bottomRight = WaitingRoomCoordinates.select(
                    Op.build {
                        WaitingRoomCoordinates.type eq BOTTOM_RIGHT_CORNER
                    }
                ).firstOrNull()?.toModel() ?: return@transaction null
                LocationCoordinates(
                    leftTop = topLeft,
                    rightBottom = bottomRight
                )
            }
        }
    }

    override suspend fun locationFlow(): Flow<LocationCoordinates?> {
        return stateFlow ?: MutableStateFlow(get()).also { flow ->
            stateFlow = flow
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(dispatchers.io) {
            transaction {
                WaitingRoomCoordinates.selectAll().empty()
            }
        }
    }

    private fun ResultRow.toModel(): Coordinates {
        return Coordinates(
            x = get(WaitingRoomCoordinates.x),
            y = get(WaitingRoomCoordinates.y),
            z = get(WaitingRoomCoordinates.z)
        )
    }

}