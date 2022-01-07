package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository

class ArenaQueueRepositoryImpl @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository
): ArenaQueueRepository {

    private val queueMap = Collections.synchronizedMap(
        mutableMapOf<Long, MutableSet<ArenaPlayer>>()
    )

    override fun put(player: ArenaPlayer) {
        val containsFraction = queueMap.containsKey(player.fractionId)
        if (containsFraction) {
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

    override fun isEmpty(): Boolean {
        queueMap.forEach { (_, value) ->
            if (value.isNotEmpty()) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(fractionId: Long): Boolean {
        return queueMap[fractionId]?.isEmpty() != false
    }

    override fun get(): Map<Long, Set<ArenaPlayer>> = queueMap.toMap()

    override fun remove(playerId: String) {
        val player = arenaPlayersRepository.getCachedPlayerById(playerId) ?: return
        val queue = queueMap[player.fractionId] ?: return
        queue.remove(player)
        queueMap[player.fractionId] = queue
    }

    override fun getAll(): List<ArenaPlayer> {
        val res = mutableListOf<ArenaPlayer>()
        queueMap.forEach { (_, value) ->
            res.addAll(value)
        }
        return res
    }

    override fun clear() {
        queueMap.clear()
    }

}