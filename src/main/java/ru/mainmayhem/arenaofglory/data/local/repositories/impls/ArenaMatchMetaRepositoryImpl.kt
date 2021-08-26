package ru.mainmayhem.arenaofglory.data.local.repositories.impls

import ru.mainmayhem.arenaofglory.data.entities.ArenaMatchMember
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*

class ArenaMatchMetaRepositoryImpl(
    private val logger: PluginLogger,
    private val fractionsRepository: FractionsRepository
): ArenaMatchMetaRepository {

    private val players = Collections.synchronizedList(mutableListOf<ArenaMatchMember>())

    private val fractions = Collections.synchronizedMap(mutableMapOf<Long, Int>())

    override fun setPlayers(players: List<ArenaPlayer>) {
        logger.info("Обновляем участников арены, сбрасываем очки фракций")
        this.players.clear()
        fractions.clear()
        this.players.addAll(
            players.map {
                ArenaMatchMember(it, 0)
            }
        )
        fractionsRepository.getCachedFractions().forEach {
            fractions[it.id] = 0
        }
    }

    override fun remove(playerId: String) {
        logger.info("Удаляем из матча игрока с id = $playerId")
        players.removeIf { it.player.id == playerId }
    }

    override fun insert(player: ArenaPlayer) {
        logger.info("Добавляем нового участника: $player")
        players.add(ArenaMatchMember(player, 0))
    }

    override fun incrementPlayerKills(playerId: String) {
        logger.info("Повышаем счетчик убийств у игрока с id = $playerId")
        val player = players.find { it.player.id == playerId }
        if (player == null){
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
        if (currentPoints == null){
            logger.warning("Фракция не найдена в данных текущего матча")
            return
        }
        fractions[fractionId] = currentPoints + points
        logger.info("Новые данные по фракциям: $fractions")
    }

    override fun getFractionsPoints(): Map<Long, Int> = fractions

    override fun getPlayers(): List<ArenaMatchMember> = players

}