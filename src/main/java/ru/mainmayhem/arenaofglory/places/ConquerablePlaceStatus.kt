package ru.mainmayhem.arenaofglory.places

/**
 * Все возможные статусы аванпостов
 */
sealed class ConquerablePlaceStatus{

    /**
     * Аванпост захватывают (атакующих > обороняющихся)
     */
    data class UnderAttack(
        val attackingPlayersDelta: Int,
        val attackingFractionId: Long
    ): ConquerablePlaceStatus()

    /**
     * Аванпост обороняют (атакующих < обороняющихся)
     */
    data class Defending(
        val defendingPlayersDelta: Int
    ): ConquerablePlaceStatus()

    /**
     * Патовая ситуация (атакующих = обороняющихся)
     */
    object Stalemate: ConquerablePlaceStatus()

    /**
     * Ничего не происходит (либо никого нет, либо есть только обороняющиеся)
     */
    object None: ConquerablePlaceStatus()

}
