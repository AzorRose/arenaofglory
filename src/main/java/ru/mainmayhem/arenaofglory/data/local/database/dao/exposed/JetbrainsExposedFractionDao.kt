package ru.mainmayhem.arenaofglory.data.local.database.dao.exposed

import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.Fraction
import ru.mainmayhem.arenaofglory.data.local.database.dao.FractionDao
import ru.mainmayhem.arenaofglory.data.local.database.tables.exposed.Fractions

class JetbrainsExposedFractionDao(
    private val dispatchers: CoroutineDispatchers
): FractionDao {

    override suspend fun getAll(): List<Fraction> {
        return withContext(dispatchers.io){
            transaction {
                Fractions.selectAll().toList().map { it.toModel() }
            }
        }
    }

    private fun ResultRow.toModel(): Fraction{
        return Fraction(
            id = get(Fractions.id),
            name = get(Fractions.name),
            nameInEnglish = get(Fractions.nameInEnglish),
            motto = get(Fractions.motto)
        )
    }

}