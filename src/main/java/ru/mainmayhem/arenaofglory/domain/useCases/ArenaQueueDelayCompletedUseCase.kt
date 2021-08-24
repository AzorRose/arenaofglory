package ru.mainmayhem.arenaofglory.domain.useCases

import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.jobs.StartMatchDelayJob
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * Логика начала матча, когда закончилось ожидание в 5 минут
 */
class ArenaQueueDelayCompletedUseCase @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val startMatchDelayJob: StartMatchDelayJob
) {

    fun handle(){
        val size = queueSize()
        logger.info("Игроков в очереди: $size")
        when(size){
            0 -> return
            1 -> kickPlayer()
            else -> startMatch()
        }
    }

    private fun kickPlayer(){
        logger.info("Кикаем игрока из комнаты ожидания")
        val playerId = arenaQueueRepository.getAll().first().id
        val player = javaPlugin.server.getPlayer(playerId) ?: return
        javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
            player.teleport(it.spawnLocation)
        }
    }

    private fun startMatch(){
        logger.info("Начинаем матч")
        val queue = arenaQueueRepository.get()
        if (allFractionsSizeEqual(queue)){
            logger.info("Игроков равное кол-во")
            logger.info("Очищаем очередь")
            arenaQueueRepository.clear()
            savePlayersInArenaMeta(queue)
            logger.info("Переносим на арену")
            teleportPlayersAndStartJob(queue)
        } else {
            logger.info("Игроков неравное кол-во")
            startMatchWithEqualPlayersSize(queue)
        }
    }

    private fun startMatchWithEqualPlayersSize(players: Map<Long, Set<ArenaPlayer>>){
        val min = getMinPlayersInFraction(players)
        logger.info("Меньшая из фракций состоит из $min игроков")
        val newPlayers = mutableMapOf<Long, Set<ArenaPlayer>>()
        players.forEach { (key, value) ->
            //берем первых кто зашел, удаляем их из очереди, кидаем на арену
            val firstMinCountPlayers = value.toList().subList(0, min)
            firstMinCountPlayers.forEach {
                arenaQueueRepository.remove(it.id)
            }
            newPlayers[key] = firstMinCountPlayers.toSet()
        }
        //todo возможно, стоит что-то написать тем игрокам, которые не попали на арену
        logger.info("Уравновесили команды, переносим на арену")
        teleportPlayersAndStartJob(players)
    }

    private fun getMinPlayersInFraction(players: Map<Long, Set<ArenaPlayer>>): Int {
        var res = 0
        players.entries.forEachIndexed { index, entry ->
            val size = entry.value.size
            when{
                index == 0 -> res = entry.value.size
                res > size -> res = size
            }
        }
        return res
    }

    private fun savePlayersInArenaMeta(players: Map<Long, Set<ArenaPlayer>>){
        val res = mutableListOf<ArenaPlayer>()
        players.values.forEach {
            res.addAll(it)
        }
        arenaMatchMetaRepository.setPlayers(res)
    }

    private fun teleportPlayersAndStartJob(players: Map<Long, Set<ArenaPlayer>>){
        startMatchDelayJob.start()
        players.forEach { (key, value) ->
            value.forEach { player ->
                arenaRespawnCoordinatesRepository.getCachedCoordinates()[key]?.let {
                    val random = it.coordinates[Random.nextInt(it.coordinates.size)]
                    val serverPlayer = javaPlugin.server.getPlayer(UUID.fromString(player.id))
                    serverPlayer?.teleport(
                        Location(
                            javaPlugin.server.getWorld(Constants.WORLD_NAME),
                            random.x.toDouble(),
                            random.y.toDouble(),
                            random.z.toDouble()
                        )
                    )
                }
            }
        }
    }

    private fun allFractionsSizeEqual(queue: Map<Long, Set<ArenaPlayer>>): Boolean{
        var firstFractionSize = 0
        queue.entries.forEachIndexed { index, entry ->
            when{
                index == 0 -> firstFractionSize = entry.value.size
                firstFractionSize != entry.value.size -> return false
            }
        }
        return true
    }

    private fun queueSize(): Int{
        val queue = arenaQueueRepository.get()
        var res = 0
        queue.forEach { (_, value) ->
            res += value.size
        }
        return res
    }

}