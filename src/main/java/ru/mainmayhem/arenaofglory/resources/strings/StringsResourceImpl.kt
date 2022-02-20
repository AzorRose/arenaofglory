package ru.mainmayhem.arenaofglory.resources.strings

import javax.inject.Inject
import ru.mainmayhem.arenaofglory.resources.strings.source.StringSource

private const val PARAM_SYMBOL = "%s"

class StringsResourceImpl @Inject constructor(
    stringSource: StringSource
): StringsResource {

    private val strings = stringSource.getStrings()

    override fun getString(key: StringKey, vararg params: Any?): String {
        var string = getString(key)
        params.forEach { param ->
            string = string.replace(PARAM_SYMBOL, param.toString())
        }
        return string
    }

    override fun getString(key: StringKey): String = strings[key] ?: error("String with key = ${key.resKey} not found")

}