package ru.mainmayhem.arenaofglory.resources.files

import java.io.File
import javax.inject.Inject
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.jarFilePath
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

class BuyerPluginDirectoryRepository @Inject constructor(
    private val logger: PluginLogger
): PluginDirectoryRepository {

    private val pluginDirectory = jarFilePath + Constants.PLUGIN_META_FOLDER_NAME + "/"

    init {
        makeDirIfNotExist()
    }

    override fun getFileByName(fileName: String): File {
        return File(pluginDirectory + fileName)
    }

    override fun getFileByNameOrNull(fileName: String): File? {
        val file = getFileByName(fileName)
        return if (file.exists()) file else null
    }

    override fun getOrCreateFileByName(fileName: String): File {
        makeDirIfNotExist()
        val file = getFileByName(fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun makeDirIfNotExist(){
        val directory = File(pluginDirectory)
        if (!directory.exists()){
            logger.info("Create directory $pluginDirectory")
            directory.mkdir()
        }
    }

}