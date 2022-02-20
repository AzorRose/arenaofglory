package ru.mainmayhem.arenaofglory.resources.files

import java.io.File

interface PluginDirectoryRepository {

    fun getFileByName(fileName: String): File

    fun getFileByNameOrNull(fileName: String): File?

    fun getOrCreateFileByName(fileName: String): File

}