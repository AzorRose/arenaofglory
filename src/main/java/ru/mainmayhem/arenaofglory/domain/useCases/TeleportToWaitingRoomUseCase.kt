package ru.mainmayhem.arenaofglory.domain.useCases

import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.asCalendar
import ru.mainmayhem.arenaofglory.data.dagger.annotations.EmptyTeamJobInstance
import ru.mainmayhem.arenaofglory.data.diffInMinutes
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.setCurrentDate
import ru.mainmayhem.arenaofglory.data.startMatchTimeMessage
import ru.mainmayhem.arenaofglory.domain.DisbalanceFinder
import ru.mainmayhem.arenaofglory.domain.providers.ClosestMatchDateProvider
import ru.mainmayhem.arenaofglory.jobs.MatchJob
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

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
    @EmptyTeamJobInstance
    private val emptyTeamJob: PluginFiniteJob,
    private val dispatchers: CoroutineDispatchers,
    private val fractionsRepository: FractionsRepository,
    private val closestMatchDateProvider: ClosestMatchDateProvider
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
        //неравное кол-во игроков на арене
        val isFractionDisbalanced = disbalanceFinder.isFractionDisbalanced(arenaPlayer.fractionId)

        val enemyFractionsInQueue = enemyFractionsInQueue(arenaPlayer.fractionId)

        var location: Location? = randomCoordinates.getLocation(world)

        if (!isFractionDisbalanced){
            arenaQueueRepository.put(arenaPlayer)
        }

        logger.info("Перемещаем игрока ${player.getShortInfo()} в комнату ожидания")

        when{
            isMatchActive && !isFractionDisbalanced && !enemyFractionsInQueue -> {
                player.sendMessage("До конца матча: ${matchJob.leftTime} мин")
            }
            isMatchActive && !isFractionDisbalanced && enemyFractionsInQueue -> {
                logger.info("В очереди обнаружены игроки из другой фракции, перемещаем их вместе с игроком")
                teleportOneFromEachFraction()
                location = null
            }
            isMatchActive && isFractionDisbalanced -> {
                logger.info("У фракции игрока меньшее кол-во участников, перемещаем сразу на арену")
                location = prepareForArena(arenaPlayer)
            }
            !isMatchActive -> player.sendMessage(startMatchTimeMessage(getTimeToStartMatch()))
        }

        if (!disbalanceFinder.hasEmptyFractions()){
            emptyTeamJob.stop()
        }

        withContext(dispatchers.main){
            location?.let {
                player.teleport(it)
            }
        }

    }

    //телепортирует на арену одного игрока из каждой команды
    private suspend fun teleportOneFromEachFraction(){
        arenaQueueRepository.get().forEach { (fractionId, players) ->
            val player = players.firstOrNull()
            if (player == null){
                logger.warning("Игроки с фракцией id = $fractionId не найдены в очереди")
            } else {
                val serverPlayer = javaPlugin.server.getPlayer(player.name)
                if (serverPlayer == null){
                    logger.warning("Игрок ${player.name} не найден на сервере")
                }
                prepareForArena(player)?.let { location ->
                    withContext(dispatchers.main){
                        serverPlayer?.teleport(location)
                    }
                }
                arenaQueueRepository.remove(player.id)
            }
        }
    }

    private fun prepareForArena(arenaPlayer: ArenaPlayer): Location?{
        val world = javaPlugin.server.getWorld(Constants.WORLD_NAME)
        val coordinates = arenaRespawnCoordinatesRepository
            .getCachedCoordinates()[arenaPlayer.fractionId]?.coordinates?.randomOrNull()
        if (coordinates == null){
            logger.warning("Не найдены точки респавна для игрока $arenaPlayer")
            return null
        }
        arenaMatchMetaRepository.insert(arenaPlayer)
        return coordinates.getLocation(world)
    }

    /**
     * Функция, проверяющая наличие в очереди хотя бы одного игрока из других фракций
     * @return true - если есть хотя бы один игрок из всех других фракций, отличной от fractionId
     */
    private fun enemyFractionsInQueue(fractionId: Long): Boolean{
        val queue = arenaQueueRepository.get()
        fractionsRepository.getCachedFractions()
            .map { it.id }
            .filter { it != fractionId }
            .forEach {
                if (queue[it].isNullOrEmpty())
                    return false
            }
        return true
    }

    private fun getRandomWRCoordinate(): Coordinates{
        val coordinates = waitingRoomCoordsRepository.getCachedCoordinates()?.coordinates
            ?: throw NullPointerException("Не найдены координаты комнаты ожидания")
        val randomInt = Random.nextInt(coordinates.size)
        return coordinates[randomInt]
    }

    private fun getTimeToStartMatch(): Long{
        val startArenaMatch = closestMatchDateProvider.provide()
        val start = startArenaMatch.asCalendar().setCurrentDate().time
        return start diffInMinutes Date()
    }

}