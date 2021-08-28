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

fun Date.asCalendar(): Calendar{
    return Calendar.getInstance().apply {
        time = this@asCalendar
    }
}

fun Calendar.setCurrentDate(): Calendar{
    val cal = Calendar.getInstance()
    set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
    set(Calendar.MONTH, cal.get(Calendar.MONTH))
    set(Calendar.YEAR, cal.get(Calendar.YEAR))
    return this
}

infix fun Date.timeEqualsWith(date: Date): Boolean{
    val first = asCalendar()
    val second = date.asCalendar()
    return first.hours() == second.hours() && first.minutes() == second.minutes()
}