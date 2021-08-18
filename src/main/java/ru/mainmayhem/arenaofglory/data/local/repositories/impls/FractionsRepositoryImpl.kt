package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository

class FractionsRepositoryImpl(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers
): FractionsRepository {

    private var cache = emptyList<Fraction>()

    init {
        coroutineScope.launch {
            val fractions = pluginDatabase.getFractionDao().getAll()
            withContext(dispatchers.main){
                cache = fractions
            }
        }
    }

    override fun getCachedFractions(): List<Fraction> = cache
}