package ru.mainmayhem.arenaofglory.data.local.repositories

import ru.mainmayhem.arenaofglory.data.entities.Fraction

/**
 * При создании асинхронно получает фракции из БД и кэширует
 */
interface FractionsRepository {

    fun getCachedFractions(): List<Fraction>

    fun getFractionById(id: Long): Fraction?

    fun getFractionByNameInEnglish(fractionName: String): Fraction?

}