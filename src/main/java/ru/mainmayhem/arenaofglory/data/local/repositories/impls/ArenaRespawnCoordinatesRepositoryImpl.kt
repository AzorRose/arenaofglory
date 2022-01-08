package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
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

    private val calculatedLocations = Collections.synchronizedMap(mutableMapOf<Long, CalculatedLocation>())

    init {
        coroutineScope.launch {
            dao.coordinatesFlow()
                .collectLatest { respawns ->
                    respawns.forEach { respawn ->
                        calculatedLocations[respawn.fractionId] = calculator.calculate(respawn.coordinates)
                    }
                }
        }
    }

    override fun getCachedCoordinates(): Map<Long, CalculatedLocation> {
        return calculatedLocations.toMap()
    }

}