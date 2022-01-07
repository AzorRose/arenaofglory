package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository

class FractionsRepositoryImpl @Inject constructor(
    pluginDatabase: PluginDatabase,
    coroutineScope: CoroutineScope
): FractionsRepository {

    private val cache = Collections.synchronizedList(mutableListOf<Fraction>())

    init {
        coroutineScope.launch {
            pluginDatabase
                .getFractionDao()
                .getFractionsFlow()
                .collectLatest {
                    cache.clear()
                    cache.addAll(it)
                }
        }
    }

    override fun getCachedFractions(): List<Fraction> = cache.toList()
}