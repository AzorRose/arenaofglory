package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.entities.Coordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler

/**
 * Класс, который проверяет телепортируется ли игрок на арену с помощью команд
 * Если да, то действие отменяется
 */
class TpToArenaEventHandler @Inject constructor(
    private val arenaCoordinatesRepository: ArenaCoordinatesRepository,
    private val coordinatesComparator: CoordinatesComparator
): BaseEventHandler<PlayerTeleportEvent>() {

    override fun handle(event: PlayerTeleportEvent) {
        val to = event.to
        val arenaLocation = arenaCoordinatesRepository.getCachedCoordinates()
        if (to == null || arenaLocation == null) {
            super.handle(event)
            return
        }
        val coordinates = Coordinates(to.x.toInt(), to.y.toInt(), to.z.toInt())
        if (event.cause == PlayerTeleportEvent.TeleportCause.COMMAND
            && coordinatesComparator.compare(coordinates, arenaLocation)
        ) {
            event.isCancelled = true
            event.player.sendMessage("Вы не можете перемещаться на арену с помощью консольных команд")
            return
        }
        super.handle(event)
    }

}