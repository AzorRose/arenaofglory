package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.PluginSettings

interface PluginSettingsRepository {

    fun getSettings(): PluginSettings

}