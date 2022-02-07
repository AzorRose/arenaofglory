package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import com.squareup.moshi.Moshi
import java.io.File
import javax.inject.Inject
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.jarFilePath
import ru.mainmayhem.arenaofglory.data.local.DbConfig
import ru.mainmayhem.arenaofglory.data.local.repositories.DbConfigFileRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val EMPTY_FIELD = ""
private const val FILE_NAME = "db_config.txt"
private const val DB_CONFIG_INDENT = "  "

class DbConfigFileRepoImpl @Inject constructor(
    private val logger: PluginLogger,
    moshi: Moshi
) : DbConfigFileRepository {

    private val defaultConfig = DbConfig(
        url = EMPTY_FIELD,
        driver = EMPTY_FIELD,
        user = EMPTY_FIELD,
        password = EMPTY_FIELD
    )

    private val dbConfigAdapter = moshi.adapter(DbConfig::class.java).indent(DB_CONFIG_INDENT)

    //TODO use PluginDirectoryRepository
    private val filePath = jarFilePath + Constants.PLUGIN_META_FOLDER_NAME + "/"

    override fun getConfigFromFile(): DbConfig {
        makeDirIfNotExist()
        val config = File(filePath + FILE_NAME)
        return if (config.exists()) {
            config.getConfig()
        } else {
            logger.info("Создание конфиг-файла БД в $filePath")
            config.createNewFile()
            defaultConfig into config
            defaultConfig
        }
    }

    private infix fun DbConfig.into(file: File) {
        file.writeBytes(dbConfigAdapter.toJson(this).toByteArray())
    }

    private fun File.getConfig(): DbConfig {
        val json = readText()
        val dbConfig = runCatching { dbConfigAdapter.fromJson(json) }.getOrNull()
            ?: throw NullPointerException("Неверная структура файла $FILE_NAME")

        if (dbConfig.url.isBlank())
            throw NullPointerException("Не найден URL в файле $FILE_NAME")
        if (dbConfig.driver.isBlank())
            throw NullPointerException("Не найден driver в файле $FILE_NAME")
        if (dbConfig.user.isBlank())
            logger.info("Не найден пользователь в файле $FILE_NAME")
        if (dbConfig.password.isBlank())
            logger.info("Не найден пароль в файле $FILE_NAME")

        return dbConfig
    }

    private fun makeDirIfNotExist() {
        val directory = File(filePath)
        if (!directory.exists()) {
            logger.info("Создание директории $filePath")
            directory.mkdir()
        }
    }

}