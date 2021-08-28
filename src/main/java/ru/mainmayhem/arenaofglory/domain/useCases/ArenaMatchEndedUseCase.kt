package ru.mainmayhem.arenaofglory.domain.useCases

import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.RewardRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*
import javax.inject.Inject

/**
 * Логика после окончания матча
 */
class ArenaMatchEndedUseCase @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val rewardRepository: RewardRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val dispatchers: CoroutineDispatchers
) {

    suspend fun handle(){
        logger.info("Матч закончен")
        kickPlayersInArena()
        kickPlayersInQueue()
        giveRewardToPlayers()
        arenaQueueRepository.clear()
        arenaMatchMetaRepository.setPlayers(emptyList())
    }

    private suspend fun kickPlayersInArena(){
        arenaMatchMetaRepository.getPlayers().forEach {
            it.player.teleportToServerSpawn()
        }
    }

    private suspend fun kickPlayersInQueue(){
        arenaQueueRepository.getAll().forEach {
            it.teleportToServerSpawn()
        }
    }

    private fun giveRewardToPlayers(){
        val reward = rewardRepository.get()
        if (reward == null){
            logger.warning("Невозможно выдать награду игрокам. Данные в БД отсутствуют, либо некорректны")
            return
        }
        if (isDraw()){
            arenaMatchMetaRepository.getPlayers().giveReward(reward.draw)
        } else{
            val descFractionPoints = arenaMatchMetaRepository.getFractionsPoints()
                .map { Pair(it.key, it.value) }
                .sortedByDescending { it.second }
            val maxPoints = descFractionPoints.first().second
            //делаю на случай, если в будущем будет больше 2 фракций
            val winningFractions = descFractionPoints
                .filter { it.second == maxPoints }
                .map { it.first }
            val winners = mutableListOf<ArenaMatchMember>()
            val losers = mutableListOf<ArenaMatchMember>()
            arenaMatchMetaRepository.getPlayers().forEach {
                if (winningFractions.contains(it.player.fractionId)){
                    winners.add(it)
                } else {
                    losers.add(it)
                }
            }
            winners.giveReward(reward.victory)
            losers.giveReward(reward.loss)
        }
    }

    private fun List<ArenaMatchMember>.giveReward(amount: Int){
        forEach {
            if (it.kills >= Constants.KILLS_AMOUNT_FOR_REWARD){
                it.player.giveReward(amount)
            } else {
                javaPlugin.server.getPlayer(
                    UUID.fromString(it.player.id)
                )?.sendMessage(
                    "Недостаточное количество убийств для получения награды " +
                            "(${it.kills}). Необходимо - ${Constants.KILLS_AMOUNT_FOR_REWARD}"
                )
            }
        }
    }

    private fun ArenaPlayer.giveReward(amount: Int){
        val player = javaPlugin.server.getPlayer(
            UUID.fromString(id)
        )
        if (player == null){
            logger.warning("Невозможно выдать награду игроку $player, игрок не найден")
            return
        }
        //todo
    }

    private suspend fun ArenaPlayer.teleportToServerSpawn(){
        withContext(dispatchers.main){
            javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
                javaPlugin.server.getPlayer(UUID.fromString(id))?.teleport(it.spawnLocation)
            }
        }
    }

    private fun isDraw(): Boolean{
        return arenaMatchMetaRepository.getFractionsPoints()
            .map { it.value }
            .toSet().size == 1
    }

}