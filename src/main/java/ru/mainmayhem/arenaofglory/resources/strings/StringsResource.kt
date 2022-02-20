package ru.mainmayhem.arenaofglory.resources.strings

interface StringsResource {

    fun getString(key: StringKey, vararg params: Any?): String

    fun getString(key: StringKey): String

}