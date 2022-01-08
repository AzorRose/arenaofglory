package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator

class WRCoordinatesRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): WaitingRoomCoordinatesRepository {

    private val dao = database.getWaitingRoomCoordinatesDao()

    private var calculatedLocation: CalculatedLocation? = null

    init {
        coroutineScope.launch {
            dao.locationFlow()
                .collectLatest { coordinates ->
                    calculatedLocation = coordinates?.let { calculator.calculate(coordinates) }
                }
        }
    }

    override fun getCachedCoordinates(): CalculatedLocation? = calculatedLocation

}