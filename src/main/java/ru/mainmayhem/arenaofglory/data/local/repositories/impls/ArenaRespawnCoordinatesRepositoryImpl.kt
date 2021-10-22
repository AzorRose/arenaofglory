package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator
import javax.inject.Inject

class ArenaRespawnCoordinatesRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): ArenaRespawnCoordinatesRepository {

    private val dao = database.getArenaRespawnCoordinatesDao()

    private var calculatedLocations: Map<Long, CalculatedLocation> = emptyMap()

    init {
        coroutineScope.launch {
            dao.coordinatesFlow()
                .map {
                    val res = mutableMapOf<Long, CalculatedLocation>()
                    it.forEach { respawn ->
                        res[respawn.fractionId] = calculator.calculate(respawn.coordinates)
                    }
                    res
                }
                .collectLatest {
                    calculatedLocations = it
                }
        }
    }

    override fun getCachedCoordinates(): Map<Long, CalculatedLocation> {
        return calculatedLocations
    }

}