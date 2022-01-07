package ru.mainmayhem.arenaofglory.data

import java.util.Calendar
import java.util.Date
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import ru.mainmayhem.arenaofglory.ArenaOfGlory
import ru.mainmayhem.arenaofglory.data.entities.Coordinates

//возвращает путь в котором находится jar-файл
val jarFilePath: String
    get() = ArenaOfGlory::class.java.protectionDomain.codeSource.location.toURI().path.run {
        //удаляем название джарника
        substring(0, lastIndexOf("/") + 1)
    }

fun Player.getShortInfo(): String {
    return "id = $uniqueId, name = $playerListName"
}

fun Calendar.hours(): Int {
    return get(Calendar.HOUR)
}

fun Calendar.minutes(): Int {
    return get(Calendar.MINUTE)
}

fun Date.asCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@asCalendar
    }
}

fun Calendar.setCurrentDate(): Calendar {
    val cal = Calendar.getInstance()
    set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
    set(Calendar.MONTH, cal.get(Calendar.MONTH))
    set(Calendar.YEAR, cal.get(Calendar.YEAR))
    return this
}

infix fun Date.timeEqualsWith(date: Date): Boolean {
    val first = asCalendar()
    val second = date.asCalendar()
    return first.hours() == second.hours() && first.minutes() == second.minutes()
}

//разница между датами в минутах
infix fun Date.diffInMinutes(date: Date): Long {
    val first = asCalendar()
    val second = date.asCalendar()
    return ((first.timeInMillis - second.timeInMillis) / Constants.MILLIS_IN_MINUTE).inc()
}

//todo вынести в файл strings
fun startMatchTimeMessage(leftTimeInMinutes: Long): String {
    return "${ChatColor.GOLD}До начала матча: ${ChatColor.YELLOW}$leftTimeInMinutes мин"
}

/**
 * Обновляет первый элемент в списке, удовлетворяющий условию.
 * В функцию update попадает элемент, который удовлетворяет условию condition.
 * Функция update должна вернуть уже обновленный элемент, который попадет в итоговый список
 */
inline fun <T> List<T>.updateFirst(
    condition: (T) -> Boolean,
    crossinline update: (T) -> T
): List<T> {
    find(condition)?.let { element ->
        val index = indexOf(element)
        val updated = update(element)
        return toMutableList().apply {
            set(index, updated)
        }
    }
    return this
}

fun Location.asCoordinates(): Coordinates {
    return Coordinates(
        x = x.toInt(),
        y = y.toInt(),
        z = z.toInt()
    )
}

fun <T> List<T>.second(): T {
    return this[1]
}

fun <T> Array<T>.second(): T {
    return this[1]
}