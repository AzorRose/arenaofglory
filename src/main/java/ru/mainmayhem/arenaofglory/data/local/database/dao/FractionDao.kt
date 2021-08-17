package ru.mainmayhem.arenaofglory.data.local.database.dao

import ru.mainmayhem.arenaofglory.data.entities.Fraction

interface FractionDao {

    suspend fun getAll(): List<Fraction>

}