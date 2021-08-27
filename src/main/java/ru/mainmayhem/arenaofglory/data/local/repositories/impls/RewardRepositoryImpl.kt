package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaReward
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.RewardRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

class RewardRepositoryImpl(
    coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val database: PluginDatabase,
    dispatchers: CoroutineDispatchers
): RewardRepository {

    private var reward: ArenaReward? = null

    init {
        coroutineScope.launch(dispatchers.main) {
            database.getRewardDao()
                .getRewardFlow()
                .collectLatest {
                    printLog(it)
                    reward = it
                }
        }
    }

    override fun get(): ArenaReward? = reward

    private fun printLog(arenaReward: ArenaReward?){
        if (arenaReward == null){
            logger.warning("Таблица с наградами не заполнена или заполнена некорректно")
        } else{
            logger.info("Получена награда из БД: $arenaReward")
        }
    }

}