package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import java.util.*

class FractionsRepositoryImpl(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope
): FractionsRepository {

    private val cache = Collections.synchronizedList(mutableListOf<Fraction>())

    init {
        coroutineScope.launch {
            val fractions = pluginDatabase.getFractionDao().getAll()
            cache.clear()
            cache.addAll(fractions)
        }
    }

    override fun getCachedFractions(): List<Fraction> = cache
}