package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.local.DbConfig

interface DbConfigFileRepository {

    /**
     * Получает данные из конфигурационного файла
     * Если файла нет, создает с дефолтными настройками
     */
    fun getConfigFromFile(): DbConfig

}