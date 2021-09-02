package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaReward
import ru.mainmayhem.arenaofglory.data.local.database.dao.RewardDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Reward
import javax.inject.Inject

class JERewardDao @Inject constructor(
    private val dispatchers: CoroutineDispatchers
): RewardDao {

    private var stateFlow: MutableStateFlow<ArenaReward?>? = null

    private val victory = "victory"
    private val draw = "draw"
    private val loss = "loss"

    override suspend fun get(): ArenaReward? {
        return withContext(dispatchers.io){
            transaction {
                val victory = Reward.select {
                    Op.build {
                        Reward.type eq victory
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null
                val draw = Reward.select {
                    Op.build {
                        Reward.type eq draw
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null
                val loss = Reward.select {
                    Op.build {
                        Reward.type eq loss
                    }
                }.firstOrNull()?.asPair()?.second ?: return@transaction null

                ArenaReward(victory, draw, loss)

            }
        }
    }

    override suspend fun insert(arenaReward: ArenaReward) {
        withContext(dispatchers.io){
            transaction {
                Reward.deleteAll()
                Reward.insert {
                    it[type] = victory
                    it[tokensAmount] = arenaReward.victory
                }
                Reward.insert {
                    it[type] = draw
                    it[tokensAmount] = arenaReward.draw
                }
                Reward.insert {
                    it[type] = loss
                    it[tokensAmount] = arenaReward.loss
                }
            }
            stateFlow?.value = get()
        }
    }

    override suspend fun getRewardFlow(): Flow<ArenaReward?> {
        return stateFlow ?: MutableStateFlow(get()).also {
            stateFlow = it
        }
    }

    private fun ResultRow.asPair(): Pair<String, Int>{
        return Pair(
            get(Reward.type),
            get(Reward.tokensAmount)
        )
    }

}