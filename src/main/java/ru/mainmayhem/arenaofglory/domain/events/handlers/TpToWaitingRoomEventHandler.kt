package ru.mainmayhem.arenaofglory.domain.events.handlers

import javax.inject.Inject
import org.bukkit.event.player.PlayerTeleportEvent
import ru.mainmayhem.arenaofglory.data.asCoordinates
import ru.mainmayhem.arenaofglory.data.local.repositories.WaitingRoomCoordinatesRepository
import ru.mainmayhem.arenaofglory.domain.CoordinatesComparator
import ru.mainmayhem.arenaofglory.domain.events.BaseEventHandler

/**
 * Класс, который проверяет телепортируется ли игрок в комнату ожидания с помощью команд
 * Если да, то действие отменяется
 */
class TpToWaitingRoomEventHandler @Inject constructor(
    private val waitingRoomCoordinatesRepository: WaitingRoomCoordinatesRepository,
    private val coordinatesComparator: CoordinatesComparator
): BaseEventHandler<PlayerTeleportEvent>() {

    override fun handle(event: PlayerTeleportEvent) {
        val to = event.to
        val waitingRoomLocation = waitingRoomCoordinatesRepository.getCachedCoordinates()
        if (to == null || waitingRoomLocation == null) {
            super.handle(event)
            return
        }
        val coordinates = to.asCoordinates()
        if (event.cause == PlayerTeleportEvent.TeleportCause.COMMAND
            && coordinatesComparator.compare(coordinates, waitingRoomLocation)
        ) {
            event.isCancelled = true
            event.player.sendMessage("Вы не можете перемещаться в комнату ожидания с помощью консольных команд")
            return
        }
        super.handle(event)
    }

}