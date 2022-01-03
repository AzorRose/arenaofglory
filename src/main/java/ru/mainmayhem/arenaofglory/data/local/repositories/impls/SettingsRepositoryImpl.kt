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
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
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
            openWaitingRoomMins = 10,
            startArenaMatch = getDefaultArenaMatchTimes(),
            minKillsForReward = 5,
            matchDuration = 15,
            fractionBoostDefeats = 2,
            outpostConqueringDuration = 30
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
            openWaitingRoomMins = openWaitingRoomMins,
            startArenaMatch = startArenaMatch.map(sdf::parse),
            minKillsForReward = minKillsForReward,
            matchDuration = matchDuration,
            fractionBoostDefeats = fractionBoostDefeats,
            outpostConqueringDuration = outpostConqueringDuration
        )
    }

    private fun makeDirIfNotExist(){
        val directory = File(filePath)
        if (!directory.exists()){
            logger.info("Создание директории $filePath")
            directory.mkdir()
        }
    }

    private fun getDefaultArenaMatchTimes(): List<String> {
        return listOf(
            "0:00",
            "4:00",
            "8:00",
            "12:00",
            "16:00",
            "20:00"
        )
    }

    private data class Settings(
        @Json(name = "open_waiting_room_mins")
        val openWaitingRoomMins: Int,
        @Json(name = "start_arena_match")
        val startArenaMatch: List<String>,
        @Json(name = "min_kills_for_reward")
        val minKillsForReward: Int,
        @Json(name = "match_duration")
        val matchDuration: Int,
        @Json(name = "fraction_boost_defeats")
        val fractionBoostDefeats: Int,
        @Json(name = "outpost_conquering_duration")
        val outpostConqueringDuration: Int
    )

}