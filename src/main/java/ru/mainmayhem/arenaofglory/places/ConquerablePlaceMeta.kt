package ru.mainmayhem.arenaofglory.places

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

abstract class ConquerablePlaceMeta {

    //Игроки, находящиеся на территории этой местности
    private val players = mutableMapOf<Long, Set<ArenaPlayer>>()
    //Состояние точки от 0 до 100
    private var state: Int = 0
    //была ли фракция уведомлена о захвате
    var wasNotified = false
        @Synchronized
        set

    private var status: ConquerablePlaceStatus = ConquerablePlaceStatus.None

    abstract fun defendingFractionId(): Long?
    abstract fun getPlaceId(): Long
    abstract fun getPlaceName(): String

    open fun addPlayer(player: ArenaPlayer){
        val players = players[player.fractionId].orEmpty()
        this.players[player.fractionId] = players.plus(player)
        calculateStatus()
    }

    fun removePlayer(playerId: String, fractionId: Long){
        val players = players[fractionId].orEmpty()
        this.players[fractionId] = players.filter { it.id != playerId }.toSet()
        calculateStatus()
    }

    fun getPlayers() = players

    fun updateState(newState: Int){
        state = newState
    }

    fun getState() = state

    fun getStatus() = status

    fun sendMessageToDefenders(message: String, javaPlugin: JavaPlugin){
        val fractionId = defendingFractionId() ?: return
        getPlayers()[fractionId]?.forEach {
            javaPlugin.server.getPlayer(it.name)?.sendMessage(message)
        }
    }

    fun sendMessageToAttackers(message: String, javaPlugin: JavaPlugin){
        getPlayers().filter {
            it.key != defendingFractionId()
        }.values.forEach {
            it.forEach { player ->
                javaPlugin.server.getPlayer(player.name)?.sendMessage(message)
            }
        }
    }

    private fun calculateStatus(){
        val res: ConquerablePlaceStatus
        val allPlayers = getAllPlayers()

        when{
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
    private fun isStalemate(): Boolean{
        val fractionMembersAmount = players.values
            .map { it.size }
            .sortedDescending()
        return fractionMembersAmount.size >= 2 && fractionMembersAmount.first() == fractionMembersAmount[1]
    }

    private fun getAttackingFractionMeta(): Pair<Long, Int>{
        val sorted = players
            .map { Pair(it.key, it.value) }
            .sortedByDescending { it.second.size }
        val attacker = sorted.first()
        val nextTeamSize = sorted.getOrNull(1)?.second?.size ?: 0
        return Pair(attacker.first, attacker.second.size - nextTeamSize)
    }

    private fun isUnderAttack(): Boolean{
        val attacker = players
            .map { Pair(it.key, it.value) }
            .maxByOrNull { it.second.size }!!
        return attacker.first != defendingFractionId()
    }

    private fun getAllPlayers(): List<ArenaPlayer>{
        val res = mutableListOf<ArenaPlayer>()
        players.values.forEach {
            res.addAll(it)
        }
        return res
    }

    private fun oneFraction(players: List<ArenaPlayer>): Boolean{
        val ids = players.map { it.fractionId }.toSet()
        return ids.size == 1
    }

    private fun onlyDefenders(players: List<ArenaPlayer>): Boolean{
        val defendingFractionId = defendingFractionId()
        players.forEach {
            if (it.fractionId != defendingFractionId){
                return false
            }
        }
        return true
    }

}