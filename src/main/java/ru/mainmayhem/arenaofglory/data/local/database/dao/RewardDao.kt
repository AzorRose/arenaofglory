package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.ArenaReward

interface RewardDao {

    suspend fun get(): ArenaReward?

    suspend fun insert(arenaReward: ArenaReward)

    suspend fun getRewardFlow(): Flow<ArenaReward?>

}