package ru.mainmayhem.arenaofglory.domain.useCases

import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.DisbalanceFinder
import ru.mainmayhem.arenaofglory.jobs.ArenaQueueDelayJob
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Логика телепортации игрока в комнату ожидания и добавления его в очередь
 * Стартует таймер ожидания если это необходимо
 */
class TeleportToWaitingRoomUseCase @Inject constructor(
    private val javaPlugin: JavaPlugin,
    private val waitingRoomCoordsRepository: WaitingRoomCoordinatesRepository,
    private val logger: PluginLogger,
    private val arenaQueueDelayJob: ArenaQueueDelayJob,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val matchJob: MatchJob,
    private val disbalanceFinder: DisbalanceFinder
) {

    @Throws(NullPointerException::class)
    fun teleport(playerId: String){
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(playerId)
            ?: throw NullPointerException("Игрок не принадлежит к фракции")
        val player = javaPlugin.server.getPlayer(UUID.fromString(playerId))
            ?: throw NullPointerException("Игрок не найден")
        val world = javaPlugin.server.getWorld(Constants.WORLD_NAME)
        val randomCoordinates = getRandomWRCoordinate()
        logger.info("Переносим игрока ${player.getShortInfo()} в комнату ожидания с координатами $randomCoordinates")
        player.teleport(
            Location(world, randomCoordinates.x.toDouble(), randomCoordinates.y.toDouble(), randomCoordinates.z.toDouble())
        )
        val isQueueEmpty = arenaQueueRepository.isEmpty()
        val isMatchActive = matchJob.isActive
        val isFractionDisbalanced = disbalanceFinder.isFractionDisbalanced(arenaPlayer.fractionId)
        arenaQueueRepository.put(arenaPlayer)
        //если очередь была пуста до добавления игрока, запускаем таймер на 5 минут
        //если матч уже запущен, то проверяем на дисбаланс
        when{
            isMatchActive && !isFractionDisbalanced -> {
                player.sendMessage("До конца матча: ${matchJob.leftTime} мин")
            }
            isMatchActive && isFractionDisbalanced -> {

            }
            isQueueEmpty -> arenaQueueDelayJob.start()
            else -> {
                player.sendMessage("До начала матча: ${arenaQueueDelayJob.leftTime} мин")
            }
        }

    }

    private fun getRandomWRCoordinate(): Coordinates{
        val coordinates = waitingRoomCoordsRepository.getCachedCoordinates()?.coordinates
            ?: throw NullPointerException("Не найдены координаты комнаты ожидания")
        val randomInt = Random.nextInt(coordinates.size)
        return coordinates[randomInt]
    }

}