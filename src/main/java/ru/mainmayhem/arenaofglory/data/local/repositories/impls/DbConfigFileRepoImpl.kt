package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import com.squareup.moshi.Moshi
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.jarFilePath
import ru.mainmayhem.arenaofglory.data.local.DbConfig
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.io.File

class DbConfigFileRepoImpl(
    private val logger: PluginLogger,
    moshi: Moshi
): DbConfigFileRepository {

    private val defaultConfig = DbConfig(
        url = "",
        driver = "",
        user = "",
        password = ""
    )

    private val dbConfigAdapter = moshi.adapter(DbConfig::class.java).indent("  ")

    private val filePath = jarFilePath + Constants.PLUGIN_META_FOLDER_NAME + "/"

    private val fileName = "db_config.txt"

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
        file.writeBytes(dbConfigAdapter.toJson(this).toByteArray())
    }

    private fun File.getConfig(): DbConfig{
        val json = readText()
        val dbConfig = kotlin.runCatching { dbConfigAdapter.fromJson(json) }.getOrNull()
            ?: throw NullPointerException("Неверная структура файла $fileName")

        if (dbConfig.url.isBlank())
            throw NullPointerException("Не найден URL в файле $fileName")
        if (dbConfig.driver.isBlank())
            throw NullPointerException("Не найден driver в файле $fileName")
        if (dbConfig.user.isBlank())
            logger.info("Не найден пользователь в файле $fileName")
        if (dbConfig.password.isBlank())
            logger.info("Не найден пароль в файле $fileName")

        return dbConfig
    }

    private fun makeDirIfNotExist(){
        val directory = File(filePath)
        if (!directory.exists()){
            logger.info("Создание директории $filePath")
            directory.mkdir()
        }
    }

}