package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CalculatedLocation
import ru.mainmayhem.arenaofglory.domain.CoordinatesCalculator
import javax.inject.Inject

class ArenaCoordinatesRepositoryImpl @Inject constructor(
    private val calculator: CoordinatesCalculator,
    database: PluginDatabase,
    coroutineScope: CoroutineScope
): ArenaCoordinatesRepository {

    private val dao = database.getArenaCoordinatesDao()

    private var calculatedLocation: CalculatedLocation? = null

    init {
        coroutineScope.launch {
            dao.locationFlow()
                .map {
                    it?.let {
                        calculator.calculate(it)
                    }
                }
                .collectLatest {
                    calculatedLocation = it
                }
        }
    }

    override fun getCachedCoordinates(): CalculatedLocation? = calculatedLocation

}