package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.entities.OutpostData
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator

class OutpostsRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): OutpostsRepository {

    private val cache = Collections.synchronizedMap(mutableMapOf<Long, OutpostData>())

    init {
        coroutineScope.launch {
            database.getOutpostsDao()
                .coordinatesFlow()
                .collectLatest { outposts ->
                    outposts.forEach { outpost ->
                        cache[outpost.id] = OutpostData(
                            outpost = outpost,
                            calculatedLocation = calculator.calculate(outpost.coordinates)
                        )
                    }
                }
        }
    }

    override fun getCachedOutposts() = cache.toMap()

}