package ru.mainmayhem.arenaofglory.domain.useCases

import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.entities.LocationCoordinates
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

/**
 * Подтягивает данные из таблиц, если они были изменены
 * Таблицы fractions, waiting_room_coordinates заполняет данными, если они были пусты
 */
class InitDataUseCase @Inject constructor(
    private val database: PluginDatabase,
    private val logger: PluginLogger
) {

    suspend fun init(){
        logger.info("Инициализация данных")
        checkFractions()
        checkWaitingRoomCoordinates()
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

}