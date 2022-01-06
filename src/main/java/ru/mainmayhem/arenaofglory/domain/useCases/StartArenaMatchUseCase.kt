package ru.mainmayhem.arenaofglory.domain.useCases

import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.dagger.annotations.StartMatchDelayJobInstance
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.providers.StartMatchEffectProvider
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob

private const val NO_PLAYERS_AMOUNT = 0
private const val NOT_ENOUGH_PLAYERS_AMOUNT = 1
private const val FIRST_LIST_INDEX = 0

/**
 * Логика начала матча, когда закончилось ожидание в 5 минут
 */
class StartArenaMatchUseCase @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    @StartMatchDelayJobInstance
    private val startMatchDelayJob: PluginFiniteJob,
    private val dispatchers: CoroutineDispatchers,
    private val startMatchEffectProvider: StartMatchEffectProvider,
    private val settingsRepository: PluginSettingsRepository
) {

    suspend fun handle(){
        val size = queueSize()
        logger.info("Игроков в очереди: $size")
        when(size){
            NO_PLAYERS_AMOUNT -> return
            NOT_ENOUGH_PLAYERS_AMOUNT -> kickPlayer()
            else -> startMatch()
        }
    }

    private suspend fun kickPlayer(){
        val arenaPlayer = arenaQueueRepository.getAll().first()
        arenaQueueRepository.remove(arenaPlayer.id)
        val player = javaPlugin.server.getPlayer(arenaPlayer.name) ?: return
        logger.info("Кикаем игрока ${player.getShortInfo()} из комнаты ожидания")
        withContext(dispatchers.main){
            javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let { world ->
                player.teleport(world.spawnLocation)
            }
            player.sendMessage("Недостаточно игроков")
        }
    }

    private suspend fun startMatch(){
        logger.info("Начинаем матч")
        val queue = arenaQueueRepository.get()
        if (allFractionsSizeEqual(queue)){
            logger.info("Игроков равное кол-во")
            logger.info("Очищаем очередь")
            arenaQueueRepository.clear()
            savePlayersInArenaMeta(queue)
            logger.info("Переносим на арену")
            teleportPlayersAndStartJob(queue)
            logger.info("Применяем эффект зелий у игроков слабой фракции")
            withContext(dispatchers.main) { updatePlayersEffects(queue) }
        } else {
            logger.info("Игроков неравное кол-во")
            startMatchWithEqualPlayersSize(queue)
        }
    }

    private suspend fun startMatchWithEqualPlayersSize(players: Map<Long, Set<ArenaPlayer>>){
        val min = getMinPlayersInFraction(players)
        logger.info("Меньшая из фракций состоит из $min игроков")
        val newPlayers = mutableMapOf<Long, Set<ArenaPlayer>>()
        players.forEach { (fractionId, players) ->
            //берем первых кто зашел, удаляем их из очереди, кидаем на арену
            val firstMinCountPlayers = players.toList().subList(FIRST_LIST_INDEX, min)
            firstMinCountPlayers.forEach { player ->
                arenaQueueRepository.remove(player.id)
            }
            newPlayers[fractionId] = firstMinCountPlayers.toSet()
        }
        //todo возможно, стоит что-то написать тем игрокам, которые не попали на арену
        logger.info("Уравновесили команды, переносим на арену")
        logger.info("Новые команды: $newPlayers")
        savePlayersInArenaMeta(newPlayers)
        teleportPlayersAndStartJob(newPlayers)
    }

    private fun getMinPlayersInFraction(players: Map<Long, Set<ArenaPlayer>>): Int {
        var res = 0
        players.entries.forEachIndexed { index, entry ->
            val size = entry.value.size
            when{
                index == FIRST_LIST_INDEX -> res = entry.value.size
                res > size -> res = size
            }
        }
        return res
    }

    private suspend fun savePlayersInArenaMeta(players: Map<Long, Set<ArenaPlayer>>){
        val res = mutableListOf<ArenaPlayer>()
        players.values.forEach { playersSet ->
            res.addAll(playersSet)
        }
        arenaMatchMetaRepository.setPlayers(res)
    }

    private suspend fun teleportPlayersAndStartJob(players: Map<Long, Set<ArenaPlayer>>){
        startMatchDelayJob.start()
        withContext(dispatchers.main){
            players.forEach { (key, value) ->
                value.forEach { player ->
                    arenaRespawnCoordinatesRepository.getCachedCoordinates()[key]?.let {
                        val random = it.coordinates[Random.nextInt(it.coordinates.size)]
                        val serverPlayer = javaPlugin.server.getPlayer(UUID.fromString(player.id))
                        serverPlayer?.teleport(
                            random.getLocation(javaPlugin.server.getWorld(Constants.WORLD_NAME))
                        )
                    }
                }
            }
        }
    }

    private fun allFractionsSizeEqual(queue: Map<Long, Set<ArenaPlayer>>): Boolean{
        var firstFractionSize = 0
        queue.entries.forEachIndexed { index, entry ->
            when{
                index == FIRST_LIST_INDEX -> firstFractionSize = entry.value.size
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

    private fun updatePlayersEffects(players: Map<Long, Set<ArenaPlayer>>) {
        val matchDuration = settingsRepository.getSettings().matchDuration
        players.forEach { (fractionId, players) ->
            val effect = startMatchEffectProvider.provideEffect(
                fractionId = fractionId,
                durationInMinutes = matchDuration
            )
            if (effect != null) {
                players.forEach { arenaPlayer ->
                    javaPlugin.server.getPlayer(arenaPlayer.name)?.addPotionEffect(effect)
                }
            }
        }
    }

}