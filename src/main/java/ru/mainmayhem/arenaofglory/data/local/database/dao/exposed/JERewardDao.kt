package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaReward
import ru.mainmayhem.arenaofglory.data.local.database.dao.RewardDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Reward

private const val VICTORY = "victory"
private const val DRAW = "draw"
private const val LOSS = "loss"

class JERewardDao @Inject constructor(
    private val dispatchers: CoroutineDispatchers
): RewardDao {

    private var stateFlow: MutableStateFlow<ArenaReward?>? = null

    override suspend fun get(): ArenaReward? {
        return withContext(dispatchers.io) {
            transaction {
                val victory = Reward.select {
                    Op.build {
                        Reward.type eq VICTORY
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null
                val draw = Reward.select {
                    Op.build {
                        Reward.type eq DRAW
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null
                val loss = Reward.select {
                    Op.build {
                        Reward.type eq LOSS
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null

                ArenaReward(victory, draw, loss)
            }
        }
    }

    override suspend fun insert(arenaReward: ArenaReward) {
        withContext(dispatchers.io) {
            transaction {
                Reward.deleteAll()
                Reward.insert {
                    it[type] = VICTORY
                    it[tokensAmount] = arenaReward.victory
                }
                Reward.insert {
                    it[type] = DRAW
                    it[tokensAmount] = arenaReward.draw
                }
                Reward.insert {
                    it[type] = LOSS
                    it[tokensAmount] = arenaReward.loss
                }
            }
            stateFlow?.value = get()
        }
    }

    override suspend fun getRewardFlow(): Flow<ArenaReward?> {
        return stateFlow ?: MutableStateFlow(get()).also { flow ->
            stateFlow = flow
        }
    }

    private fun ResultRow.asPair(): Pair<String, Int> {
        return Pair(
            get(Reward.type),
            get(Reward.tokensAmount)
        )
    }

}