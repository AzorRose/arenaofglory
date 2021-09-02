package ru.mainmayhem.arenaofglory.data.local.database.dao

import kotlinx.coroutines.flow.Flow
import ru.mainmayhem.arenaofglory.data.entities.Fraction

interface FractionDao {

    suspend fun getAll(): List<Fraction>

    suspend fun insert(fractions: List<Fraction>)

    suspend fun getFractionsFlow(): Flow<List<Fraction>>

}