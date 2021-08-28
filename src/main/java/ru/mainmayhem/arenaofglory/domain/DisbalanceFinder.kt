package ru.mainmayhem.arenaofglory.domain

import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import javax.inject.Inject

/**
 * Класс, который находит те команды, где игроков меньше
 */
class DisbalanceFinder @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val fractionsRepository: FractionsRepository
) {

    /**
     * Находит все фракции с меньшим кол-вом игроков
     */
    fun findDisbalancedFractions(): List<Long>{
        val mapData = getArenaMeta()
        val listByPlayers = mapData.sortedBy { it.members.size }
        if (listByPlayers.isEmpty()) return emptyList()
        val maxSize = listByPlayers.last().members.size
        return mapData
            .filter { it.members.size < maxSize }
            .map { it.fractionId }
    }

    /**
     * Имеет ли фракция дисбаланс по игрокам
     */
    fun isFractionDisbalanced(fractionId: Long): Boolean{
        return findDisbalancedFractions().contains(fractionId)
    }

    /**
     * Есть ли в матче пустые команды
     */
    fun hasEmptyFractions(): Boolean{
        return getArenaMeta().any { it.members.isEmpty() }
    }

    private fun getArenaMeta(): List<ArenaFraction>{
        val map = mutableMapOf<Long, List<ArenaPlayer>>()
        val players = arenaMatchMetaRepository.getPlayers()
        //добавляем все фракции в мапу
        fractionsRepository.getCachedFractions()
            .forEach {
                map[it.id] = emptyList()
            }
        //распределяем игроков пр фракциям
        players.forEach {
            val list = map[it.player.fractionId].orEmpty()
            map[it.player.fractionId] = list.plus(it.player)
        }
        val result = mutableListOf<ArenaFraction>()
        //конвертим в список, чтобы удобнее можно было сортировать
        map.map {
            result.add(ArenaFraction(fractionId = it.key, members = it.value))
        }
        return result
    }

    private data class ArenaFraction(
        val fractionId: Long,
        val members: List<ArenaPlayer>
    )

}