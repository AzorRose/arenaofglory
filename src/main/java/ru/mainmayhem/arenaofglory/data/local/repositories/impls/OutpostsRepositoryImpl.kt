package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.Outpost
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator
import javax.inject.Inject

class OutpostsRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): OutpostsRepository {

    private var cache = emptyList<Pair<Outpost, CalculatedLocation>>()

    init {
        coroutineScope.launch {
            database.getOutpostsDao()
                .coordinatesFlow()
                .map { outposts ->
                    outposts.map {
                        Pair(it, calculator.calculate(it.coordinates))
                    }
                }
                .collectLatest {
                    cache = it
                }
        }
    }

    override fun getCachedOutposts(): List<Pair<Outpost, CalculatedLocation>> = cache

}