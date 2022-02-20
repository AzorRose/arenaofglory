package ru.mainmayhem.arenaofglory.resources.strings.source

import ru.mainmayhem.arenaofglory.resources.strings.StringKey

interface StringSource {

    fun getStrings(): Map<StringKey, String>

}