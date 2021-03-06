package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Command
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.entities.Outpost
import ru.mainmayhem.arenaofglory.data.local.database.dao.OutpostsDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Outposts

private const val REWARD_COMMANDS_SEPARATOR = ";"

class JetbrainsExposedOutpostsDao @Inject constructor(
    private val dispatchers: CoroutineDispatchers
): OutpostsDao {

    private var stateFlow: MutableStateFlow<List<Outpost>>? = null

    override suspend fun insert(outposts: List<Outpost>) {
        withContext(dispatchers.io) {
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
                        it[rewardCommands] =
                            outpost.rewardCommands.joinToString(REWARD_COMMANDS_SEPARATOR) { command -> command.cmd }
                    }
                }
            }
            stateFlow?.value = get()
        }
    }

    override suspend fun get(): List<Outpost> {
        return withContext(dispatchers.io) {
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
                        ),
                        rewardCommands = it[Outposts.rewardCommands]
                            .orEmpty()
                            .split(REWARD_COMMANDS_SEPARATOR)
                            .filter { str -> str.isNotBlank() }
                            .map { cmd -> Command(cmd) }
                    )
                }
            }
        }
    }

    override suspend fun coordinatesFlow(): Flow<List<Outpost>> {
        return stateFlow ?: MutableStateFlow(get()).also { flow ->
            stateFlow = flow
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(dispatchers.io) {
            transaction {
                Outposts.selectAll().empty()
            }
        }
    }

    override suspend fun changeOwner(outpostId: Long, ownerFractionId: Long) {
        withContext(dispatchers.io) {
            transaction {
                Outposts.update({ Outposts.id eq outpostId }) {
                    it[fractionId] = ownerFractionId
                }
            }
            stateFlow?.value = get()
        }
    }

}