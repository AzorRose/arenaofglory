package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator

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
                .map { respawns ->
                    val res = mutableMapOf<Long, CalculatedLocation>()
                    respawns.forEach { respawn ->
                        res[respawn.fractionId] = calculator.calculate(respawn.coordinates)
                    }
                    res
                }
                .collectLatest { mapLocations ->
                    calculatedLocations = mapLocations
                }
        }
    }

    override fun getCachedCoordinates(): Map<Long, CalculatedLocation> {
        return calculatedLocations
    }

}