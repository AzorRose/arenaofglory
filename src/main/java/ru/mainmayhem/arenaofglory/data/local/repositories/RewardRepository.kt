package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.ArenaReward

interface RewardRepository {

    fun get(): ArenaReward?

}