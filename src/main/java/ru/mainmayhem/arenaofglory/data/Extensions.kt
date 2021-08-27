package ru.mainmayhem.arenaofglory.data

import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.ArenaOfGlory
import java.util.*

//возвращает путь в котором находится jar-файл
val jarFilePath: String
    get() = ArenaOfGlory::class.java.protectionDomain.codeSource.location.toURI().path.run {
        //удаляем название джарника
        substring(0, lastIndexOf("/") + 1)
    }

fun Player.getShortInfo(): String{
    return "id = $uniqueId, name = $playerListName"
}

fun Calendar.hours(): Int{
    return get(Calendar.HOUR)
}

fun Calendar.minutes(): Int{
    return get(Calendar.MINUTE)
}