package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.PluginSettings
import ru.mainmayhem.arenaofglory.data.jarFilePath
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SettingsRepositoryImpl(
    moshi: Moshi,
    private val logger: PluginLogger
): PluginSettingsRepository {

    private val fileName = "settings.txt"

    private val settingsAdapter = moshi.adapter(Settings::class.java).indent("  ")

    private val filePath = jarFilePath + Constants.PLUGIN_META_FOLDER_NAME + "/"

    private val pluginSettings: PluginSettings by lazy {
        makeDirIfNotExist()
        val settings = File(filePath + fileName)
        if (settings.exists()){
            settings.getSettings()
        } else {
            logger.info("Создание файла с настройками  в $filePath")
            settings.createNewFile()
            settings.fillWithDefaultSettings()
        }
    }

    override fun getSettings(): PluginSettings = pluginSettings

    private fun File.fillWithDefaultSettings(): PluginSettings{
        val default = Settings(
            openWaitingRoom = "18:50",
            startArenaMatch = "19:00",
            minKillsForReward = 5,
            matchDuration = 15
        )
        writeBytes(settingsAdapter.toJson(default).toByteArray())
        return default.toModel()
    }

    private fun File.getSettings(): PluginSettings{
        val content = readText()
        if (content.isBlank())
            throw RuntimeException("Файл $fileName пуст")
        val settings = kotlin.runCatching { settingsAdapter.fromJson(content) }.getOrNull()
            ?: throw RuntimeException("Некорректный формат записи в файле $fileName")
        return settings.toModel()
    }

    private fun Settings.toModel(): PluginSettings{
        val timePattern = "HH:mm"
        val sdf = SimpleDateFormat(timePattern, Locale.getDefault())
        return PluginSettings(
            openWaitingRoom = sdf.parse(openWaitingRoom),
            startArenaMatch = sdf.parse(startArenaMatch),
            minKillsForReward = minKillsForReward,
            matchDuration = matchDuration
        )
    }

    private fun makeDirIfNotExist(){
        val directory = File(filePath)
        if (!directory.exists()){
            logger.info("Создание директории $filePath")
            directory.mkdir()
        }
    }

    private data class Settings(
        @Json(name = "open_waiting_room")
        val openWaitingRoom: String,
        @Json(name = "start_arena_match")
        val startArenaMatch: String,
        @Json(name = "min_kills_for_reward")
        val minKillsForReward: Int,
        @Json(name = "match_duration")
        val matchDuration: Int
    )

}