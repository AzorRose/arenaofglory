package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.getShortInfo
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaMatchMetaRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaRespawnCoordinatesRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler
import ru.mainmayhem.arenaofglory.domain.providers.StartMatchEffectProvider

private const val POTION_EFFECT_TICKS_DELAY = 1L

/**
 * Если игрок участник арены, респавним его на территории его фракции
 */
class ArenaRespawnEventHandler @Inject constructor(
    private val arenaMatchMetaRepository: ArenaMatchMetaRepository,
    private val arenaRespawnCoordinatesRepository: ArenaRespawnCoordinatesRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val startMatchEffectProvider: StartMatchEffectProvider
): BaseEventHandler<PlayerRespawnEvent>() {

    override fun handle(event: PlayerRespawnEvent) {
        val playerId = event.player.uniqueId.toString()
        val player = arenaMatchMetaRepository.getPlayerById(playerId)
        if (player != null) {
            val coordinate = getRandomCoordinate(player.player.fractionId)
            if (coordinate == null) {
                logger.error(
                    className = "ArenaRespawnEventHandler",
                    methodName = "handle",
                    throwable = NullPointerException("Не найден респавн для игрока ${event.player.getShortInfo()}")
                )
            } else {
                event.respawnLocation = coordinate.getLocation(javaPlugin.server.getWorld(Constants.WORLD_NAME))
                val fractionId = player.player.fractionId
                startMatchEffectProvider.provideEffect(fractionId, false)?.let { effect ->
                    Bukkit.getScheduler().scheduleSyncDelayedTask(
                        javaPlugin,
                        { event.player.addPotionEffect(effect) },
                        POTION_EFFECT_TICKS_DELAY
                    )
                }
            }
        }
        super.handle(event)
    }

    private fun getRandomCoordinate(fractionId: Long): Coordinates? {
        val respawn = arenaRespawnCoordinatesRepository.getCachedCoordinates()[fractionId] ?: return null
        return respawn.coordinates.random()
    }

}