package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.withContext
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao

class JetbrainsExposedFractionDao(
    private val dispatchers: CoroutineDispatchers
): FractionDao {

    override suspend fun getAll(): List<Fraction> {
        //todo
        withContext(dispatchers.io){

        }
        return emptyList()
    }

}