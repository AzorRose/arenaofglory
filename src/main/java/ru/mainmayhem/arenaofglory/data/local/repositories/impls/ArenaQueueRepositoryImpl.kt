package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import java.util.*

class ArenaQueueRepositoryImpl: ArenaQueueRepository {

    private val queueMap = Collections.synchronizedMap(
        mutableMapOf<Long, MutableSet<ArenaPlayer>>()
    )

    override fun put(player: ArenaPlayer) {
        val containsFraction = queueMap.containsKey(player.fractionId)
        if (containsFraction){
            val players = queueMap[player.fractionId]!!
            players.add(player)
            queueMap[player.fractionId] = players
        } else {
            queueMap[player.fractionId] = mutableSetOf(player)
        }
    }

    override fun getAndRemove(fractionId: Long): ArenaPlayer? {
        val players = queueMap[fractionId]
        val result = players?.firstOrNull()
        result ?: return null
        players.remove(result)
        queueMap[fractionId] = players
        return result
    }

    override fun isEmpty(): Boolean = queueMap.isEmpty()

    override fun isEmpty(fractionId: Long): Boolean{
        return queueMap[fractionId]?.isEmpty() != false
    }

}