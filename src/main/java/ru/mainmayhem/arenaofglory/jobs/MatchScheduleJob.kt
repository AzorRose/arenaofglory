package ru.mainmayhem.arenaofglory.jobs

import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.asCalendar
import ru.mainmayhem.arenaofglory.data.diffInMinutes
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.setCurrentDate
import ru.mainmayhem.arenaofglory.data.startMatchTimeMessage
import ru.mainmayhem.arenaofglory.data.timeEqualsWith
import ru.mainmayhem.arenaofglory.domain.WaitingRoomScheduleHelper
import ru.mainmayhem.arenaofglory.domain.providers.ClosestMatchDateProvider
import ru.mainmayhem.arenaofglory.domain.useCases.StartArenaMatchUseCase

private const val JOB_DELAY_SECONDS = 1L
private const val JOB_EVENT_DELAY_MILLIS = 60_000L

class MatchScheduleJob @Inject constructor(
    coroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
    logger: PluginLogger,
    private val settingsRepository: PluginSettingsRepository,
    private val startArenaMatchUseCase: StartArenaMatchUseCase,
    private val javaPlugin: JavaPlugin,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val waitingRoomScheduleHelper: WaitingRoomScheduleHelper,
    private val closestMatchDateProvider: ClosestMatchDateProvider
): PluginCoroutineJob(
    coroutineScope = coroutineScope,
    dispatchers = dispatchers,
    logger = logger,
    delay = JOB_DELAY_SECONDS,
    delayTimeUnit = TimeUnit.SECONDS
) {

    private var startArenaMatch = closestMatchDateProvider.provide()
    private var openWaitingRoom = getOpenWaitingRoomDate()

    override suspend fun doRepeatedlyInBackground() {
        val date = Date()
        when {
            date timeEqualsWith openWaitingRoom -> {
                sendMessageToAllPlayers(
                    title = "Война за честь и славу скоро начнется!!",
                    subtitle = "Все подробности вам расскажет фракционный наставник Кайл!"
                )
                delay(JOB_EVENT_DELAY_MILLIS)//чтобы не спамило
            }
            date timeEqualsWith startArenaMatch -> {
                startArenaMatchUseCase.handle()
                delay(JOB_EVENT_DELAY_MILLIS)
                //достаем время следующего матча именно после задержки, чтобы не достать текущее время
                startArenaMatch = closestMatchDateProvider.provide()
                openWaitingRoom = getOpenWaitingRoomDate()
            }
            waitingRoomScheduleHelper.preparingForMatch() -> {
                val start = startArenaMatch.asCalendar().setCurrentDate().time
                val diff = start diffInMinutes date
                sendMessageToPlayersInQueue(startMatchTimeMessage(diff))
                delay(JOB_EVENT_DELAY_MILLIS)
            }
        }
    }

    private fun sendMessageToAllPlayers(title: String, subtitle: String) {
        val titleFontSettings = ChatColor.GOLD.toString()
        val subtitleFontSettings = ChatColor.GREEN.toString()
        javaPlugin.server.onlinePlayers.forEach {
            it.sendTitle(
                titleFontSettings + title,
                subtitleFontSettings + subtitle,
                10,
                70,
                20
            )
        }
    }

    private fun sendMessageToPlayersInQueue(message: String) {
        arenaQueueRepository.getAll().forEach { player ->
            javaPlugin.server.getPlayer(player.name)?.sendMessage(message)
        }
    }

    private fun getOpenWaitingRoomDate(): Date {
        val openWaitingRoomTimeMillis =
            settingsRepository.getSettings().openWaitingRoomMins * Constants.MILLIS_IN_MINUTE
        return Date(startArenaMatch.time - openWaitingRoomTimeMillis)
    }

}