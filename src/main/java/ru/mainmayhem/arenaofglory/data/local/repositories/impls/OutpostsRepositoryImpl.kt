package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.Outpost
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator

class OutpostsRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): OutpostsRepository {

    private val cache = Collections.synchronizedMap(mutableMapOf<Outpost, CalculatedLocation>())

    init {
        coroutineScope.launch {
            database.getOutpostsDao()
                .coordinatesFlow()
                .collectLatest { outposts ->
                    outposts.forEach { outpost ->
                        cache.clear()
                        cache[outpost] = calculator.calculate(outpost.coordinates)
                    }
                }
        }
    }

    override fun getCachedOutposts() = cache.toMap()

}