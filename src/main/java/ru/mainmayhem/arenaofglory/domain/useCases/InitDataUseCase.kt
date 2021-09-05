package ru.mainmayhem.arenaofglory.domain.useCases

import ru.mainmayhem.arenaofglory.data.entities.*
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

/**
 * Заполняет таблицы данными по умолчанию, если они были пусты
 */
class InitDataUseCase @Inject constructor(
    private val database: PluginDatabase,
    private val logger: PluginLogger
) {

    suspend fun init(){
        logger.info("Инициализация данных")
        checkFractions()
        checkWaitingRoomCoordinates()
        checkArenaRespawns()
        checkReward()
        checkArenaCoordinates()
    }

    private suspend fun checkFractions(){
        logger.info("Проверка фракций")
        val dao = database.getFractionDao()
        if (dao.getAll().isNotEmpty()) return
        logger.info("Таблица с фракциями пуста, заполняем данными")
        val fractions = listOf(
            Fraction(
                id = 1,
                name = "Капелла",
                nameInEnglish = "kapella",
                motto = "Несущие свет"
            ),
            Fraction(
                id = 2,
                name = "Процион",
                nameInEnglish = "procion",
                motto = "Несущие бурю"
            )
        )
        dao.insert(fractions)
        logger.info("Таблица с фракциями заполнена данными по умолчанию: $fractions")
    }

    private suspend fun checkWaitingRoomCoordinates(){
        logger.info("Проверка координат комнаты ожидания")
        val dao = database.getWaitingRoomCoordinatesDao()
        if (dao.isEmpty().not()) return
        logger.info("Таблица с координатами комнаты ожидания пуста, заполняем данными")
        val locationCoordinates = LocationCoordinates(
            leftTop = Coordinates(0,0,0),
            rightBottom = Coordinates(10,10,10)
        )
        dao.insert(locationCoordinates)
        logger.info("Таблица заполнена данными по умолчанию: $locationCoordinates")
    }

    private suspend fun checkArenaRespawns(){
        logger.info("Проверка респавнов на арене")
        val dao = database.getArenaRespawnCoordinatesDao()
        if (!dao.isEmpty()) return
        logger.info("Таблица с координатами респавнов пуста, заполняем данными")
        val respawns = listOf<RespawnCoordinates>(
            RespawnCoordinates(
                fractionId = 1,
                coordinates = LocationCoordinates(
                    leftTop = Coordinates(
                        x = 0,
                        y = 0,
                        z = 0
                    ),
                    rightBottom = Coordinates(
                        x = 20,
                        y = 20,
                        z = 20
                    )
                )
            ),
            RespawnCoordinates(
                fractionId = 2,
                coordinates = LocationCoordinates(
                    leftTop = Coordinates(
                        x = 0,
                        y = 0,
                        z = 0
                    ),
                    rightBottom = Coordinates(
                        x = 20,
                        y = 20,
                        z = 20
                    )
                )
            )
        )
        dao.insert(respawns)
        logger.info("Таблица с координатами респавнов заполнена данными по умолчанию: $respawns")
    }

    private suspend fun checkReward(){
        logger.info("Проверка награды")
        val dao = database.getRewardDao()
        if (dao.get() != null) return
        logger.info("Таблица с наградами пуста, заполняем значениями по умолчанию")
        val reward = ArenaReward(
            victory = 2,
            draw = 1,
            loss = 1
        )
        dao.insert(reward)
    }

    private suspend fun checkArenaCoordinates(){
        logger.info("Проверка координат арены")
        val dao = database.getArenaCoordinatesDao()
        if (dao.isEmpty().not()) return
        logger.info("Таблица с координатами арены пуста, заполняем данными")
        val locationCoordinates = LocationCoordinates(
            leftTop = Coordinates(0,0,0),
            rightBottom = Coordinates(10,10,10)
        )
        dao.insert(locationCoordinates)
        logger.info("Таблица заполнена данными по умолчанию: $locationCoordinates")
    }

}