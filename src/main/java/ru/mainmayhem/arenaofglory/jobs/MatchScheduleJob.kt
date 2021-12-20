package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.*
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.*
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.WaitingRoomScheduleHelper
import ru.mainmayhem.arenaofglory.domain.providers.ClosestMatchDateProvider
import ru.mainmayhem.arenaofglory.domain.useCases.StartArenaMatchUseCase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchScheduleJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    private val settingsRepository: PluginSettingsRepository,
    private val startArenaMatchUseCase: StartArenaMatchUseCase,
    private val javaPlugin: JavaPlugin,
    private val arenaQueueRepository: ArenaQueueRepository,
    private val waitingRoomScheduleHelper: WaitingRoomScheduleHelper,
    private val closestMatchDateProvider: ClosestMatchDateProvider
) {

    private var job: Job? = null

    private var startArenaMatch = closestMatchDateProvider.provide()
    private var openWaitingRoom = getOpenWaitingRoomDate()

    fun start(){
        if (job?.isActive == true)
            return
        job = coroutineScope.launch {
            try {
                while (isActive){
                    val date = Date()
                    when{
                        date timeEqualsWith openWaitingRoom-> {
                            sendMessageToAllPlayers(
                                title = "Война за честь и славу скоро начнется!!",
                                subtitle = "Все подробности вам расскажет фракционный наставник Кайл!"
                            )
                            delay(60_000)//чтобы не спамило
                        }
                        date timeEqualsWith startArenaMatch -> {
                            startArenaMatchUseCase.handle()
                            delay(60_000)
                            //достаем время следующего матча именно после задержки, чтобы не достать текущее время
                            startArenaMatch = closestMatchDateProvider.provide()
                            openWaitingRoom = getOpenWaitingRoomDate()
                        }
                        waitingRoomScheduleHelper.preparingForMatch() -> {
                            val start = startArenaMatch.asCalendar().setCurrentDate().time
                            val diff = start diffInMinutes date
                            sendMessageToPlayersInQueue(startMatchTimeMessage(diff))
                            delay(60_000)
                        }
                        else -> delay(1_000)
                    }
                }
            } catch (t: Throwable){
                if (t !is CancellationException) {
                    logger.error(
                        className = "MatchScheduleJob",
                        methodName = "job",
                        throwable = t
                    )
                }
            }
        }
    }

    fun stop(){
        job?.cancel(CancellationException())
        job = null
    }

    private fun sendMessageToAllPlayers(title: String, subtitle: String){
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

    private fun sendMessageToPlayersInQueue(message: String){
        arenaQueueRepository.getAll().forEach {
            javaPlugin.server.getPlayer(UUID.fromString(it.id))?.sendMessage(message)
        }
    }

    private fun getOpenWaitingRoomDate(): Date {
        val openWaitingRoomTimeMillis = settingsRepository.getSettings().openWaitingRoomMins * Constants.MILLIS_IN_MINUTE
        return Date(startArenaMatch.time - openWaitingRoomTimeMillis)
    }

}