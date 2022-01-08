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

    private val fractionsById = Collections.synchronizedMap(mutableMapOf<Long, Fraction>())
    private val fractionsByNameInEnglish = Collections.synchronizedMap(mutableMapOf<String, Fraction>())

    init {
        coroutineScope.launch {
            pluginDatabase
                .getFractionDao()
                .getFractionsFlow()
                .collectLatest { fractions ->
                    fractions.forEach { fraction ->
                        fractionsById[fraction.id] = fraction
                        fractionsByNameInEnglish[fraction.nameInEnglish] = fraction
                    }
                }
        }
    }

    override fun getCachedFractions(): List<Fraction> = fractionsById.values.toList()

    override fun getFractionById(id: Long): Fraction? = fractionsById[id]

    override fun getFractionByNameInEnglish(fractionName: String): Fraction? = fractionsByNameInEnglish[fractionName]

}