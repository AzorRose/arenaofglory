package ru.mainmayhem.arenaofglory.domain.useCases

import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class TeleportToWaitingRoomUseCase @Inject constructor(
    private val javaPlugin: JavaPlugin,
    private val waitingRoomCoordsRepository: WaitingRoomCoordinatesRepository,
    private val logger: PluginLogger
) {

    @Throws(NullPointerException::class)
    fun teleport(playerId: String){
        val player = javaPlugin.server.getPlayer(UUID.fromString(playerId))
            ?: throw NullPointerException("Игрок не найден")
        //fixme название мира лучше все же вынести в БД
        val world = javaPlugin.server.getWorld("world") ?: throw NullPointerException("Мир для телепортации не найден")
        val randomCoordinates = getRandomWRCoordinate()
        logger.info("Переносим игрока ${player.getShortInfo()} в комнату ожидания с координатами $randomCoordinates")
        player.teleport(
            Location(world, randomCoordinates.x.toDouble(), randomCoordinates.y.toDouble(), randomCoordinates.z.toDouble())
        )
    }

    private fun getRandomWRCoordinate(): Coordinates{
        val coordinates = waitingRoomCoordsRepository.getCachedCoordinates()?.coordinates
            ?: throw NullPointerException("Не найдены координаты комнаты ожидания")
        val randomInt = Random.nextInt(coordinates.size)
        return coordinates[randomInt]
    }

}