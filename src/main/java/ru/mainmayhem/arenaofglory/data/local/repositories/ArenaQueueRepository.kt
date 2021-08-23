package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer

/**
 * Репозиторий для хранения игроков, которые находятся в очереди
 * У каждой фракции своя очередь
 */
interface ArenaQueueRepository {

    /**
     * Добавляет игрока в очередь своей фракции
     */
    fun put(player: ArenaPlayer)

    /**
     * Берет первого игрока из очереди фракции
     * @param fractionId - id фракции из которой нужно взять игрока
     */
    fun getAndRemove(fractionId: Long): ArenaPlayer?

    /**
     * Пуста ли очередь на арену
     * @return true - очередь на арену пуста (вне зависимости от фракции)
     */
    fun isEmpty(): Boolean

    /**
     * Пуста ли очередь конкретной фракции
     * @param fractionId - id фракции у которой нужно проверить очередь
     * @return true - очередь фракции пуста или не существует
     */
    fun isEmpty(fractionId: Long): Boolean

    /**
     * Получение очередей из игроков по каждой фракции
     * key - id фракции, value - сет из игроков
     */
    fun get(): Map<Long, Set<ArenaPlayer>>

    /**
     * Исключает игрока из очереди
     * @param playerId - id игрока, которого надо удалить из очереди
     */
    fun remove(playerId: String)

    /**
     * Возвращает всех игроков вперемешку
     */
    fun getAll(): List<ArenaPlayer>

    /**
     * Полностью очищает очередь
     */
    fun clear()

}