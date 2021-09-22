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
import ru.mainmayhem.arenaofglory.data.entities.Outpost
import ru.mainmayhem.arenaofglory.data.local.database.dao.OutpostsDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Outposts

class JetbrainsExposedOutpostsDao(
    private val dispatchers: CoroutineDispatchers
): OutpostsDao {

    private var stateFlow: MutableStateFlow<List<Outpost>>? = null

    override suspend fun insert(outposts: List<Outpost>) {
        withContext(dispatchers.io){
            transaction {
                outposts.forEach { outpost ->
                    Outposts.insert {
                        it[id] = outpost.id
                        it[fractionId] = outpost.fractionId
                        it[name] = outpost.name
                        it[fractionId] = outpost.fractionId
                        it[topLeftCornerX] = outpost.coordinates.leftTop.x
                        it[topLeftCornerY] = outpost.coordinates.leftTop.y
                        it[topLeftCornerZ] = outpost.coordinates.leftTop.z
                        it[bottomRightCornerX] = outpost.coordinates.rightBottom.x
                        it[bottomRightCornerY] = outpost.coordinates.rightBottom.y
                        it[bottomRightCornerZ] = outpost.coordinates.rightBottom.z
                    }
                }
            }
            stateFlow?.value = get()
        }
    }

    override suspend fun get(): List<Outpost> {
        return withContext(dispatchers.io){
            transaction {
                Outposts.selectAll().map {
                    Outpost(
                        id = it[Outposts.id],
                        fractionId = it[Outposts.fractionId],
                        name = it[Outposts.name],
                        coordinates = LocationCoordinates(
                            leftTop = Coordinates(
                                x = it[Outposts.topLeftCornerX],
                                y = it[Outposts.topLeftCornerY],
                                z = it[Outposts.topLeftCornerZ]
                            ),
                            rightBottom = Coordinates(
                                x = it[Outposts.bottomRightCornerX],
                                y = it[Outposts.bottomRightCornerY],
                                z = it[Outposts.bottomRightCornerZ],
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun coordinatesFlow(): Flow<List<Outpost>> {
        return stateFlow ?: MutableStateFlow(get()).also {
            stateFlow = it
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(dispatchers.io){
            transaction {
                Outposts.selectAll().empty()
            }
        }
    }

}