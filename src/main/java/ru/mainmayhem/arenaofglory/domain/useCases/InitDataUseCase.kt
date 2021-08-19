package ru.mainmayhem.arenaofglory.domain.useCases

import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

/**
 * Подтягивает данные из таблиц, если они были изменены
 * Таблицу fractions заполняет данными, если она пуста
 */
class InitDataUseCase @Inject constructor(
    private val database: PluginDatabase,
    private val logger: PluginLogger
) {

    suspend fun init(){
        logger.info("Инициализация данных")
        checkFractions()
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

}