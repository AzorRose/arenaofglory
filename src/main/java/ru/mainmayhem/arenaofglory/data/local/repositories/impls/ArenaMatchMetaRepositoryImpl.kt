package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import java.util.Collections
import javax.inject.Inject
import kotlinx.coroutines.withContext
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val ZERO_KILLS = 0
private const val ZERO_FRACTION_POINTS = 0

class ArenaMatchMetaRepositoryImpl @Inject constructor(
    private val logger: PluginLogger,
    private val fractionsRepository: FractionsRepository,
    private val dispatchers: CoroutineDispatchers
): ArenaMatchMetaRepository {

    private val players = Collections.synchronizedList(mutableListOf<ArenaMatchMember>())
    private val fractions = Collections.synchronizedMap(mutableMapOf<Long, Int>())

    override suspend fun setPlayers(players: List<ArenaPlayer>) {
        withContext(dispatchers.main) {
            logger.info("Обновляем участников арены, сбрасываем очки фракций")
            this@ArenaMatchMetaRepositoryImpl.players.clear()
            fractions.clear()
            this@ArenaMatchMetaRepositoryImpl.players.addAll(
                players.map { player ->
                    ArenaMatchMember(player, ZERO_KILLS)
                }
            )
            fractionsRepository.getCachedFractions().forEach { fraction ->
                fractions[fraction.id] = ZERO_FRACTION_POINTS
            }
        }
    }

    override fun remove(playerId: String) {
        logger.info("Удаляем из матча игрока с id = $playerId")
        players.removeIf { it.player.id == playerId }
    }

    override fun insert(player: ArenaPlayer) {
        logger.info("Добавляем нового участника: $player")
        players.add(ArenaMatchMember(player, ZERO_KILLS))
    }

    override fun incrementPlayerKills(playerId: String) {
        logger.info("Повышаем счетчик убийств у игрока с id = $playerId")
        val player = players.find { player -> player.player.id == playerId }
        if (player == null) {
            logger.warning("Игрок не найден в данных текущего матча")
            return
        }
        val index = players.indexOf(player)
        players[index] = player.copy(kills = player.kills.inc())
        logger.info("Новые данные по игрокам: $players")
    }

    override fun increaseFractionPoints(fractionId: Long, points: Int) {
        logger.info("Повышаем счетчик у фракции с id = $fractionId")
        val currentPoints = fractions[fractionId]
        if (currentPoints == null) {
            logger.warning("Фракция не найдена в данных текущего матча")
            return
        }
        val newPoints = currentPoints + points
        fractions[fractionId] = newPoints
        logger.info("Новые данные по фракциям: $fractions")
    }

    override fun getFractionsPoints(): Map<Long, Int> = fractions.toMap()

    override fun getPlayers(): List<ArenaMatchMember> = players.toList()

    override fun clear() {
        players.clear()
        fractions.clear()
    }

}