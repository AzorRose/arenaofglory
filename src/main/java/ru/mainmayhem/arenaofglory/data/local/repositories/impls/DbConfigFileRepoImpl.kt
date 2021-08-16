package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.jarFilePath
import ru.mainmayhem.arenaofglory.data.local.DbConfig
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.io.File

class DbConfigFileRepoImpl(
    private val logger: PluginLogger
): DbConfigFileRepository {

    private val defaultConfig = DbConfig(
        url = "",
        driver = "",
        user = null,
        password = null
    )

    private val filePath = jarFilePath + Constants.PLUGIN_META_FOLDER_NAME + "/"

    private val fileName = "db_config"

    private val urlConfigName = "url"
    private val driverConfigName = "driver"
    private val userConfigName = "user"
    private val passwordConfigName = "password"

    override fun getConfigFromFile(): DbConfig {
        makeDirIfNotExist()
        val config = File(filePath + fileName)
        return if (config.exists()){
            config.getConfig()
        } else {
            logger.info("Создание конфиг-файла БД в $filePath")
            config.createNewFile()
            defaultConfig into config
            defaultConfig
        }
    }

    private infix fun DbConfig.into(file: File){
        val content =
                "$urlConfigName=$url\n" +
                "$driverConfigName=$driver\n" +
                "$userConfigName=$user\n" +
                "$passwordConfigName=$password"
        file.writeBytes(content.toByteArray())
    }

    private fun File.getConfig(): DbConfig{
        val lines = readLines()
        val url = lines.firstOrNull()?.split("=")?.getOrNull(1)
        val driver = lines.getOrNull(1)?.split("=")?.getOrNull(1)
        val user = lines.getOrNull(2)?.split("=")?.getOrNull(1)
        val password = lines.getOrNull(3)?.split("=")?.getOrNull(1)
        if (url == null)
            throw NullPointerException("Не найден URL в файле $fileName")
        if (driver == null)
            throw NullPointerException("Не найден driver в файле $fileName")
        if (user == null)
            logger.info("Не найден пользователь в файле $userConfigName")
        if (password == null)
            logger.info("Не найден пароль в файле $userConfigName")
        return DbConfig(url, driver, user, password)
    }

    private fun makeDirIfNotExist(){
        val directory = File(filePath)
        if (!directory.exists()){
            logger.info("Создание директории $filePath")
            directory.mkdir()
        }
    }

}