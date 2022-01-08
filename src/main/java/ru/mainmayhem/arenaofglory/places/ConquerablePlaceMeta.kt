package ru.mainmayhem.arenaofglory.places

import java.text.DecimalFormat
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.second

abstract class ConquerablePlaceMeta {

    companion object {
        const val MIN_PLACE_STATE = 0.0
        const val MAX_PLACE_STATE = 100.0
        private const val FORMATTED_STATE_PATTERN = "0.##"
    }

    //Игроки, находящиеся на территории этой местности
    private val players = mutableMapOf<Long, Set<ArenaPlayer>>()

    //Состояние точки от 0 до 100
    @Volatile
    private var state: Double = MIN_PLACE_STATE

    //была ли фракция уведомлена о захвате
    var wasNotified = false

    private var status: ConquerablePlaceStatus = ConquerablePlaceStatus.None

    abstract fun defendingFractionId(): Long?
    abstract fun getPlaceId(): Long
    abstract fun getPlaceName(): String

    @Synchronized
    open fun addPlayer(player: ArenaPlayer) {
        val players = players[player.fractionId].orEmpty()
        this.players[player.fractionId] = players.plus(player)
        calculateStatus()
    }

    @Synchronized
    fun removePlayer(playerId: String, fractionId: Long) {
        val players = players[fractionId].orEmpty()
        this.players[fractionId] = players.filter { it.id != playerId }.toSet()
        calculateStatus()
    }

    fun getPlayers() = players.toMap()

    fun updateState(newState: Double) {
        state = newState
    }

    fun getState() = state

    fun getFormattedState(): String {
        val formatter = DecimalFormat(FORMATTED_STATE_PATTERN)
        return formatter.format(state)
    }

    fun getStatus() = status

    fun sendMessageToDefenders(message: String, javaPlugin: JavaPlugin) {
        val fractionId = defendingFractionId() ?: return
        getPlayers()[fractionId]?.forEach {
            javaPlugin.server.getPlayer(it.name)?.sendMessage(message)
        }
    }

    fun sendMessageToAttackers(message: String, javaPlugin: JavaPlugin) {
        getPlayers().filter {
            it.key != defendingFractionId()
        }.values.forEach {
            it.forEach { player ->
                javaPlugin.server.getPlayer(player.name)?.sendMessage(message)
            }
        }
    }

    private fun calculateStatus() {
        val res: ConquerablePlaceStatus
        val allPlayers = getAllPlayers()

        when {
            allPlayers.isEmpty() || onlyDefenders(allPlayers) -> {
                res = ConquerablePlaceStatus.None
            }
            //точка нейтральна и присутствуют только игроки из одной фракции
            defendingFractionId() == null && allPlayers.isNotEmpty() && oneFraction(allPlayers) -> {
                res = ConquerablePlaceStatus.UnderAttack(
                    attackingPlayersDelta = allPlayers.size,
                    attackingFractionId = allPlayers.first().fractionId
                )
            }
            isStalemate() -> {
                res = ConquerablePlaceStatus.Stalemate
            }
            defendingFractionId() == null || isUnderAttack() -> {
                val meta = getAttackingFractionMeta()
                res = ConquerablePlaceStatus.UnderAttack(
                    attackingFractionId = meta.first,
                    attackingPlayersDelta = meta.second
                )
            }
            else -> {
                val meta = getAttackingFractionMeta()
                res = ConquerablePlaceStatus.Defending(meta.second)
            }
        }

        status = res
    }

    //патовая ситуация
    private fun isStalemate(): Boolean {
        val fractionMembersAmount = players.values
            .map { it.size }
            .sortedDescending()
        return fractionMembersAmount.size >= 2 && fractionMembersAmount.first() == fractionMembersAmount.second()
    }

    private fun getAttackingFractionMeta(): Pair<Long, Int> {
        val sorted = players
            .map { Pair(it.key, it.value) }
            .sortedByDescending { it.second.size }
        val attacker = sorted.first()
        val nextTeamSize = sorted.getOrNull(1)?.second?.size ?: 0
        return Pair(attacker.first, attacker.second.size - nextTeamSize)
    }

    private fun isUnderAttack(): Boolean {
        val attacker = players
            .maxByOrNull { (_, players) -> players.size }!!
        return attacker.key != defendingFractionId()
    }

    private fun getAllPlayers(): List<ArenaPlayer> {
        return players.values.flatten()
    }

    private fun oneFraction(players: List<ArenaPlayer>): Boolean {
        return players.distinctBy { player -> player.fractionId }.size == 1
    }

    private fun onlyDefenders(players: List<ArenaPlayer>): Boolean {
        val defendingFractionId = defendingFractionId()
        players.forEach { arenaPlayer ->
            if (arenaPlayer.fractionId != defendingFractionId) {
                return false
            }
        }
        return true
    }

}