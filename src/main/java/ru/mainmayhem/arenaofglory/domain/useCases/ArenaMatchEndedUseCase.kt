package ru.mainmayhem.arenaofglory.domain.useCases

import kotlinx.coroutines.withContext
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.*
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.inventory.items.token.TokenFactory
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
    private val dispatchers: CoroutineDispatchers,
    private val tokenFactory: TokenFactory,
    private val fractionsRepository: FractionsRepository,
    private val settingsRepository: PluginSettingsRepository,
    private val db: PluginDatabase,
    private val matchResultsRepository: MatchResultsRepository
) {

    /**
     * @param autoWin - команда полностью вышла и время ожидания истекло
     * Считаем, что команда, которая осталась на арене, победила
     */
    suspend fun handle(autoWin: Boolean){
        logger.info("Матч закончен")
        withContext(dispatchers.default){
            saveMatchResults()
            printResults(autoWin)
            updatePlayersKills()
            kickPlayersInArena()
            kickPlayersInQueue()
            giveRewardToPlayers(autoWin)
            arenaQueueRepository.clear()
            arenaMatchMetaRepository.clear()
        }
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

    private fun giveRewardToPlayers(autoWin: Boolean){
        val reward = rewardRepository.get()
        if (reward == null){
            logger.warning("Невозможно выдать награду игрокам. Данные в БД отсутствуют, либо некорректны")
            return
        }
        when{
            autoWin -> {
                arenaMatchMetaRepository.getPlayers().giveReward(reward.victory, autoWin)
            }
            isDraw() -> {
                arenaMatchMetaRepository.getPlayers().giveReward(reward.draw, autoWin)
            }
            else -> {
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
                winners.giveReward(reward.victory, autoWin)
                losers.giveReward(reward.loss, autoWin)
            }
        }
    }

    private fun List<ArenaMatchMember>.giveReward(amount: Int, autoWin: Boolean){
        val minKills = settingsRepository.getSettings().minKillsForReward
        forEach {
            if (it.kills >= minKills || autoWin){
                it.player.giveReward(amount)
            } else {
                printNotEnoughKillsMessage(
                    playerName = it.player.name,
                    neededKills = minKills,
                    playerKills = it.kills
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
        player.inventory.addItem(tokenFactory.getTokens(amount))
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

    private fun getWinningFractionName(): String?{
        val id = getWinningFractionId() ?: return null
        return fractionsRepository.getCachedFractions().find { it.id == id }?.name
    }

    private fun getWinningFractionId(): Long? {
        return arenaMatchMetaRepository.getFractionsPoints()
            .maxByOrNull { it.value }?.key
    }

    private fun getLooserFractionId(): Long? {
        return arenaMatchMetaRepository.getFractionsPoints()
            .minByOrNull { it.value }?.key
    }

    private fun getAutoWonFractionName(): String?{
        //так как у нас 2 команды, то при автопобеде на арене будет одна фракция
        val fractionId = arenaMatchMetaRepository.getPlayers().firstOrNull()?.player?.fractionId
        return fractionId?.let {fraction ->
            fractionsRepository.getCachedFractions().find { it.id == fraction }?.name
        }
    }

    private fun printResults(autoWin: Boolean){
        val title = "${ChatColor.GOLD}Битва за честь и славу была закончена."
        val subtitleFontSettings = ChatColor.YELLOW.toString()
        val fractionFontSettings = ChatColor.DARK_RED.toString()
        when{
            autoWin -> {
                sendMessageToAllPlayers(
                    title,
                    "${subtitleFontSettings}Победителем в ней стала нация - " +
                            "${fractionFontSettings + getAutoWonFractionName()} ${subtitleFontSettings}!!!"
                )
            }
            isDraw() -> {
                sendMessageToAllPlayers(title, "${subtitleFontSettings}В этой кровавой схватке победить не был определён!!")
            }
            else -> {
                sendMessageToAllPlayers(
                    title,
                    "${subtitleFontSettings}Победителем в ней стала нация - " +
                            "${fractionFontSettings + getWinningFractionName()} ${subtitleFontSettings}!!!"
                )
            }
        }
    }

    private fun sendMessageToAllPlayers(title: String, subtitle: String){
        javaPlugin.server.onlinePlayers.forEach {
            it.sendTitle(
                title,
                subtitle,
                10,
                70,
                20
            )
        }
    }

    private fun printNotEnoughKillsMessage(
        playerName: String,
        playerKills: Int,
        neededKills: Int
    ){
        val commonFontSettings = ChatColor.AQUA.toString()
        val paramsFontSettings = ChatColor.GOLD.toString()
        javaPlugin.server.getPlayer(playerName)?.sendMessage(
            "${commonFontSettings}Вы бились как чемпион, но в этот раз этого было недостаточно. " +
                    "Вами повержено ${paramsFontSettings+playerKills} ${commonFontSettings}противников, для получения " +
                    "награды требуется - ${"$paramsFontSettings$neededKills $commonFontSettings !"}"
        )
    }

    private suspend fun updatePlayersKills(){
        logger.info("Обновляем общее кол-во убийств у игроков")
        arenaMatchMetaRepository.getPlayers().forEach {
            try {
                db.getArenaPlayersDao().increaseKills(
                    playerId = it.player.id,
                    playerKills = it.kills
                )
            } catch (t: Throwable){
                logger.error(
                    className = "ArenaMatchEndedUseCase",
                    methodName = "updatePlayersKills",
                    message = "Не удалось обновить кол-во убийств для игрока ${it.player}",
                    throwable = t
                )
            }
        }
    }

    private suspend fun saveMatchResults() {
        if (isDraw()) {
            matchResultsRepository.addDrawResult()
            return
        }
        val winner = getWinningFractionId() ?: return
        val looser = getLooserFractionId() ?: return
        matchResultsRepository.addResult(winner, looser)
    }

}