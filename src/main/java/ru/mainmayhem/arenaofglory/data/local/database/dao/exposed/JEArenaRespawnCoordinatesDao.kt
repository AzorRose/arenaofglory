package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.entities.RespawnCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.dao.ArenaRespawnCoordinatesDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.ArenaRespawnCoordinates

class JEArenaRespawnCoordinatesDao(
    private val dispatchers: CoroutineDispatchers
): ArenaRespawnCoordinatesDao {

    private var stateFlow: MutableStateFlow<List<RespawnCoordinates>>? = null

    override suspend fun insert(respawnCoordinates: List<RespawnCoordinates>) {
        withContext(dispatchers.io){
            transaction {
                respawnCoordinates.forEach { resp ->
                    ArenaRespawnCoordinates.insert {
                        it[fractionId] = resp.fractionId
                        it[topLeftCornerX] = resp.coordinates.leftTop.x
                        it[topLeftCornerY] = resp.coordinates.leftTop.y
                        it[topLeftCornerZ] = resp.coordinates.leftTop.z
                        it[bottomRightCornerX] = resp.coordinates.rightBottom.x
                        it[bottomRightCornerY] = resp.coordinates.rightBottom.y
                        it[bottomRightCornerZ] = resp.coordinates.rightBottom.z
                    }
                }
            }
            stateFlow?.value = get()
        }
    }

    override suspend fun get(): List<RespawnCoordinates> {
        return withContext(dispatchers.io){
            transaction {
                ArenaRespawnCoordinates.selectAll().map {
                    RespawnCoordinates(
                        fractionId = it[ArenaRespawnCoordinates.fractionId],
                        coordinates = LocationCoordinates(
                            leftTop = Coordinates(
                                x = it[ArenaRespawnCoordinates.topLeftCornerX],
                                y = it[ArenaRespawnCoordinates.topLeftCornerY],
                                z = it[ArenaRespawnCoordinates.topLeftCornerZ]
                            ),
                            rightBottom = Coordinates(
                                x = it[ArenaRespawnCoordinates.bottomRightCornerX],
                                y = it[ArenaRespawnCoordinates.bottomRightCornerY],
                                z = it[ArenaRespawnCoordinates.bottomRightCornerZ],
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun locationFlow(): Flow<List<RespawnCoordinates>?> {
        return stateFlow ?: MutableStateFlow(get()).also {
            stateFlow = it
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(dispatchers.io){
            transaction {
                ArenaRespawnCoordinates.selectAll().empty()
            }
        }
    }

}