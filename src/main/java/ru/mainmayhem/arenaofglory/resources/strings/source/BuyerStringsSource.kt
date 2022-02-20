package ru.mainmayhem.arenaofglory.resources.strings.source

import java.io.File
import javax.inject.Inject
import ru.mainmayhem.arenaofglory.resources.files.PluginDirectoryRepository
import ru.mainmayhem.arenaofglory.resources.strings.StringKey

private const val STRINGS_FILE_NAME = "strings.txt"
private const val KEY_VALUE_SEPARATOR = ":"

class BuyerStringsSource @Inject constructor(
    pluginDirectoryRepository: PluginDirectoryRepository
): StringSource {

    private val stringRes: Map<StringKey, String> by lazy {
        val file = pluginDirectoryRepository.getFileByNameOrNull(STRINGS_FILE_NAME)
            ?: error("Strings file not found. Please create file $STRINGS_FILE_NAME")
        parse(file)
    }

    private val stringKeyMap = StringKey.values().associateBy(StringKey::resKey)

    override fun getStrings(): Map<StringKey, String> = stringRes

    private fun parse(file: File): Map<StringKey, String> {
        val result = mutableMapOf<StringKey, String>()
        file.readLines()
            .forEach { line ->
                line.substringBefore(KEY_VALUE_SEPARATOR)
                    .trim()
                    .removePrefix("\"")
                    .removeSuffix("\"")
                    .toKey()
                    ?.let { key ->
                        result[key] = line.substringAfter(KEY_VALUE_SEPARATOR)
                    }
            }
        return result
    }

    private fun String.toKey(): StringKey? {
        return stringKeyMap[this]
    }

}