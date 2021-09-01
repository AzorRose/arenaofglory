package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*

class ArenaMatchMetaRepositoryImpl(
    private val logger: PluginLogger,
    private val fractionsRepository: FractionsRepository,
    private val javaPlugin: JavaPlugin,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val dispatchers: CoroutineDispatchers
): ArenaMatchMetaRepository {

    private val players = Collections.synchronizedList(mutableListOf<ArenaMatchMember>())

    private val fractions = Collections.synchronizedMap(mutableMapOf<Long, Int>())

    private var arenaScoreBoard: ArenaScoreBoard? = null


    override suspend fun setPlayers(players: List<ArenaPlayer>) {
        withContext(dispatchers.main){
            logger.info("Обновляем участников арены, сбрасываем очки фракций")
            this@ArenaMatchMetaRepositoryImpl.players.clear()
            fractions.clear()
            this@ArenaMatchMetaRepositoryImpl.players.addAll(
                players.map {
                    ArenaMatchMember(it, 0)
                }
            )
            arenaScoreBoard = ArenaScoreBoard()
            fractionsRepository.getCachedFractions().forEach {fraction ->
                fractions[fraction.id] = 0
            }
            players.forEach { arenaPlayer ->
                arenaScoreBoard?.addToTeam(arenaPlayer)
            }
        }
    }

    override fun remove(playerId: String) {
        logger.info("Удаляем из матча игрока с id = $playerId")
        players.removeIf { it.player.id == playerId }
        arenaScoreBoard?.removeFromTeam(playerId)
    }

    override fun insert(player: ArenaPlayer) {
        logger.info("Добавляем нового участника: $player")
        players.add(ArenaMatchMember(player, 0))
        arenaScoreBoard?.addToTeam(player)
    }

    override fun incrementPlayerKills(playerId: String) {
        logger.info("Повышаем счетчик убийств у игрока с id = $playerId")
        val player = players.find { it.player.id == playerId }
        if (player == null){
            logger.warning("Игрок не найден в данных текущего матча")
            return
        }
        //scoreBoard.getObjective("name")?.getScore(player.player.name)?.score = player.kills.inc()
        val index = players.indexOf(player)
        players[index] = player.copy(kills = player.kills.inc())
        logger.info("Новые данные по игрокам: $players")
    }

    override fun increaseFractionPoints(fractionId: Long, points: Int) {
        logger.info("Повышаем счетчик у фракции с id = $fractionId")
        val currentPoints = fractions[fractionId]
        if (currentPoints == null){
            logger.warning("Фракция не найдена в данных текущего матча")
            return
        }
        val fractionName = fractionsRepository.getCachedFractions().find { it.id == fractionId }?.name.orEmpty()
        val newPoints = currentPoints + points
        fractions[fractionId] = newPoints
        arenaScoreBoard?.setNewFractionPoints(fractionName, newPoints)
        logger.info("Новые данные по фракциям: $fractions")
    }

    override fun getFractionsPoints(): Map<Long, Int> = fractions

    override fun getPlayers(): List<ArenaMatchMember> = players

    override fun clear() {
        players.forEach {
            arenaScoreBoard?.removeFromTeam(it.player.id)
        }
        arenaScoreBoard = null
        players.clear()
        fractions.clear()
    }

    private fun getPlayer(playerId: String): Player?{
        return javaPlugin.server.getPlayer(UUID.fromString(playerId))
    }

    private inner class ArenaScoreBoard{

        private val fractionsObjective: Objective

        private val scoreBoard = Bukkit.getScoreboardManager()!!.newScoreboard.apply {
            //все критерии описаны тут https://minecraft.fandom.com/wiki/Scoreboard#Criteria
            fractionsObjective = registerNewObjective("fractions_stats", "dummy", "Статистика фракций").apply {
                displaySlot = DisplaySlot.SIDEBAR
            }
        }

        init {
            fractionsRepository.getCachedFractions().forEach {fraction ->
                scoreBoard.registerNewTeam(fraction.name).also {
                    it.prefix = "[${fraction.name}] \n"
                    it.setAllowFriendlyFire(false)
                }
                fractionsObjective.getScore(fraction.name).score = 0
            }
        }

        fun addToTeam(arenaPlayer: ArenaPlayer){
            val fractionName = fractionsRepository.getCachedFractions()
                .find { it.id == arenaPlayer.fractionId }?.name
                .orEmpty()
            getPlayer(arenaPlayer.id)?.let {
                scoreBoard.getTeam(fractionName)?.addEntry(it.name)
                it.scoreboard = scoreBoard
            }
        }

        fun setNewFractionPoints(fractionName: String, newPoints: Int){
            fractionsObjective.getScore(fractionName).score = newPoints
        }

        fun removeFromTeam(playerId: String){
            val fractionId = arenaPlayersRepository.getCachedPlayerById(playerId)?.fractionId
            val fractionName = fractionsRepository.getCachedFractions()
                .find { it.id == fractionId }?.name
                .orEmpty()
            getPlayer(playerId)?.let {
                scoreBoard.getTeam(fractionName)?.removeEntry(it.name)
            }
        }

    }

}