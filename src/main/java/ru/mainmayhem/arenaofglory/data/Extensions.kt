package ru.mainmayhem.arenaofglory.data

import ru.mainmayhem.arenaofglory.ArenaOfGlory

//возвращает путь в котором находится jar-файл
val jarFilePath: String
    get() = ArenaOfGlory::class.java.protectionDomain.codeSource.location.toURI().path.run {
        //удаляем название джарника
        substring(0, lastIndexOf("/") + 1)
    }