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

    private val playersMap = Collections.synchronizedMap(mutableMapOf<String, ArenaMatchMember>())
    private val playersList: List<ArenaMatchMember>
        get() = playersMap.values.toList()
    private val fractions = Collections.synchronizedMap(mutableMapOf<Long, Int>())

    override suspend fun setPlayers(players: List<ArenaPlayer>) {
        withContext(dispatchers.main) {
            logger.info("Обновляем участников арены, сбрасываем очки фракций")
            playersMap.clear()
            fractions.clear()
            players.forEach { player ->
                playersMap[player.id] = ArenaMatchMember(player, ZERO_KILLS)
            }
            fractionsRepository.getCachedFractions().forEach { fraction ->
                fractions[fraction.id] = ZERO_FRACTION_POINTS
            }
        }
    }

    override fun remove(playerId: String) {
        logger.info("Удаляем из матча игрока с id = $playerId")
        playersMap.remove(playerId)
    }

    override fun insert(player: ArenaPlayer) {
        logger.info("Добавляем нового участника: $player")
        playersMap[player.id] = ArenaMatchMember(player, ZERO_KILLS)
    }

    override fun incrementPlayerKills(playerId: String) {
        logger.info("Повышаем счетчик убийств у игрока с id = $playerId")
        val player = playersMap[playerId]
        if (player == null) {
            logger.warning("Игрок не найден в данных текущего матча")
            return
        }
        playersMap[playerId] = player.copy(kills = player.kills.inc())
        logger.info("Новые данные по игрокам: $playersList")
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

    override fun getPlayers(): List<ArenaMatchMember> = playersList

    override fun getPlayerById(playerId: String): ArenaMatchMember? = playersMap[playerId]

    override fun clear() {
        playersMap.clear()
        fractions.clear()
    }

}