package ru.mainmayhem.arenaofglory.data

import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.ArenaOfGlory

//возвращает путь в котором находится jar-файл
val jarFilePath: String
    get() = ArenaOfGlory::class.java.protectionDomain.codeSource.location.toURI().path.run {
        //удаляем название джарника
        substring(0, lastIndexOf("/") + 1)
    }

fun Player.getShortInfo(): String{
    return "id = $uniqueId, name = $playerListName"
}