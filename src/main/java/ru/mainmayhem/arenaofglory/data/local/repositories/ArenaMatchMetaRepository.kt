package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

interface ArenaMatchMetaRepository {

    /**
     * Заменяет текущий список игроков на новый, сбрасывает очки фракций
     * Нужно в начале матча
     */
    suspend fun setPlayers(players: List<ArenaPlayer>)

    /**
     * Удаляет игрока
     * Например, когда он по какой-то причине вышел в середине матча
     */
    fun remove(playerId: String)

    /**
     * Добавляет игрока
     * Например, если он зашел из очереди во время игры
     */
    fun insert(player: ArenaPlayer)

    /**
     * Увеличивает счетчик убийств у игрока
     */
    fun incrementPlayerKills(playerId: String)

    /**
     * Увеличивает очки у фракции
     * @param points - на какое кол-во очков нужно увеличить
     */
    fun increaseFractionPoints(fractionId: Long, points: Int)

    /**
     * Возвращает кол-во очков по фракциям
     * @return key - id фракции, value - кол-во очков
     */
    fun getFractionsPoints(): Map<Long, Int>

    /**
     * Возвращает всех участников матча
     */
    fun getPlayers(): List<ArenaMatchMember>

    /**
     * Очистить данные, нужно после матча
     */
    fun clear()

}