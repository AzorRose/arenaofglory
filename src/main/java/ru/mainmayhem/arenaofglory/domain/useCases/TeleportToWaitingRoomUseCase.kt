package ru.mainmayhem.arenaofglory.domain.useCases

import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.*
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.*
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.DisbalanceFinder
import ru.mainmayhem.arenaofglory.jobs.EmptyTeamJob
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Логика телепортации игрока в комнату ожидания и добавления его в очередь
 * Если очередь пуста - стартует таймер ожидания на 5 мин, перемещаем игрока в комнату ожидания
 * Если есть дисбаланс команд, то игроки с соответствующей фракцией заходят сразу на арену
 */
class TeleportToWaitingRoomUseCase @Inject constructor(
    private val javaPlugin: JavaPlugin,
    private val waitingRoomCoordsRepository: WaitingRoomCoordinatesRepository,
    private val logger: PluginLogger,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val matchJob: MatchJob,
    private val disbalanceFinder: DisbalanceFinder,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val emptyTeamJob: EmptyTeamJob,
    private val dispatchers: CoroutineDispatchers,
    private val settingsRepository: PluginSettingsRepository
) {

    @Throws(NullPointerException::class)
    suspend fun teleport(playerId: String){
        val arenaPlayer = arenaPlayersRepository.getCachedPlayerById(playerId)
            ?: throw NullPointerException("Игрок не принадлежит к фракции")
        val player = javaPlugin.server.getPlayer(UUID.fromString(playerId))
            ?: throw NullPointerException("Игрок не найден")
        val world = javaPlugin.server.getWorld(Constants.WORLD_NAME)
        val randomCoordinates = getRandomWRCoordinate()

        val isMatchActive = matchJob.isActive
        val isFractionDisbalanced = disbalanceFinder.isFractionDisbalanced(arenaPlayer.fractionId)

        var location = randomCoordinates.getLocation(world)

        if (!isFractionDisbalanced){
            arenaQueueRepository.put(arenaPlayer)
        }

        when{
            isMatchActive && !isFractionDisbalanced -> {
                player.sendMessage("До конца матча: ${matchJob.leftTime} мин")
            }
            isMatchActive && isFractionDisbalanced -> {
                val coordinates = arenaRespawnCoordinatesRepository
                    .getCachedCoordinates()[arenaPlayer.fractionId]?.coordinates?.randomOrNull()
                if (coordinates == null){
                    logger.warning("Не найдены точки респавна для игрока $this")
                    return
                }
                arenaMatchMetaRepository.insert(arenaPlayer)
                location = coordinates.getLocation(world)
            }
            !isMatchActive -> player.sendMessage("До начала матча: ${getTimeToStartMatch()} мин")
        }

        if (!disbalanceFinder.hasEmptyFractions()){
            emptyTeamJob.stop()
        }

        withContext(dispatchers.main){
            player.teleport(location)
        }

    }

    private fun getRandomWRCoordinate(): Coordinates{
        val coordinates = waitingRoomCoordsRepository.getCachedCoordinates()?.coordinates
            ?: throw NullPointerException("Не найдены координаты комнаты ожидания")
        val randomInt = Random.nextInt(coordinates.size)
        return coordinates[randomInt]
    }

    private fun getTimeToStartMatch(): Long{
        val startArenaMatch = settingsRepository.getSettings().startArenaMatch
        val start = startArenaMatch.asCalendar().setCurrentDate().time
        return start diffInMinutes Date()
    }

}