package ru.mainmayhem.arenaofglory

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.ChangeFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.ChooseFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.EnterWaitingRoomCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.HelpCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.QuitWaitingRoomCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.ReloadPluginCommandExecutor
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchScheduleJobInstance
import ru.mainmayhem.arenaofglory.data.dagger.components.DaggerAppComponent
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.InitDataUseCase
import ru.mainmayhem.arenaofglory.domain.useCases.KickAllArenaPLayersUseCase
import ru.mainmayhem.arenaofglory.jobs.OutpostsJob
import ru.mainmayhem.arenaofglory.jobs.PluginJob
import ru.mainmayhem.arenaofglory.placeholders.FractionPlaceholders
import ru.mainmayhem.arenaofglory.placeholders.OutpostPlaceholders
import ru.mainmayhem.arenaofglory.placeholders.PlayersPlaceholders

private const val PLACEHOLDER_API_NAME = "PlaceholderAPI"
private const val CURRENT_SERVER_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"

class ArenaOfGlory: JavaPlugin() {

    //commands
    @Inject
    lateinit var chooseFractionCommandExecutor: ChooseFractionCommandExecutor
    @Inject
    lateinit var changeFractionCommandExecutor: ChangeFractionCommandExecutor
    @Inject
    lateinit var enterWaitingRoomCommandExecutor: EnterWaitingRoomCommandExecutor
    @Inject
    lateinit var quitWaitingRoomCommandExecutor: QuitWaitingRoomCommandExecutor
    @Inject
    lateinit var reloadPluginCommandExecutor: ReloadPluginCommandExecutor

    @Inject
    lateinit var initDataUseCase: InitDataUseCase
    @Inject
    lateinit var kickAllArenaPLayersUseCase: KickAllArenaPLayersUseCase

    @Inject
    lateinit var coroutineScope: CoroutineScope

    @Inject
    lateinit var logger: PluginLogger

    @Inject
    lateinit var eventsListener: EventsListener

    @Inject
    @MatchScheduleJobInstance
    lateinit var matchScheduleJob: PluginJob

    @Inject
    lateinit var outpostsJob: OutpostsJob

    //placeholders
    @Inject
    lateinit var fractionPlaceholders: FractionPlaceholders
    @Inject
    lateinit var playersPlaceholders: PlayersPlaceholders
    @Inject
    lateinit var outpostPlaceholders: OutpostPlaceholders

    override fun onEnable() {
        initDI()
        printCurrentServerDate()
        initPlaceholders()
        initData()
        server.pluginManager.registerEvents(eventsListener, this)
        initCommands()
        matchScheduleJob.start()
        outpostsJob.start()
    }

    override fun onDisable() {
        kickAllArenaPLayersUseCase.doKickPlayers()
        matchScheduleJob.stop()
        outpostsJob.stop()
        coroutineScope.cancel(CancellationException())
        clearExecutorCommands()
        HandlerList.unregisterAll(eventsListener)
        DIHolder.clear()
    }

    private fun initData() {
        coroutineScope.launch {
            kotlin.runCatching {
                initDataUseCase.init()
            }.exceptionOrNull()?.let { error ->
                logger.error(
                    methodName = "initData",
                    className = "ArenaOfGlory",
                    throwable = error
                )
            }
        }
    }

    private fun initDI() {
        DIHolder.setComponent(
            DaggerAppComponent.factory().create(this)
        )
        DIHolder.getComponent().injectArenaOfGloryClass(this)
    }

    private fun initCommands() {
        Commands.values().forEach { command ->
            getCommand(command.cmdName)!!.setExecutor(
                when (command) {
                    Commands.HELP -> HelpCommandExecutor()
                    Commands.RELOAD_PLUGIN -> reloadPluginCommandExecutor
                    Commands.CHOOSE_FRACTION -> chooseFractionCommandExecutor
                    Commands.CHANGE_FRACTION -> changeFractionCommandExecutor
                    Commands.ENTER_WAITING_ROOM -> enterWaitingRoomCommandExecutor
                    Commands.QUIT_WAITING_ROOM -> quitWaitingRoomCommandExecutor
                }
            )
        }
    }

    private fun clearExecutorCommands() {
        Commands.values().forEach { command ->
            getCommand(command.cmdName)!!.setExecutor(null)
        }
    }

    private fun printCurrentServerDate() {
        val sdf = SimpleDateFormat(CURRENT_SERVER_TIME_PATTERN, Locale.getDefault())
        logger.info("Серверное время: ${sdf.format(Date())}")
    }

    private fun initPlaceholders() {
        val placeHolders: List<PlaceholderExpansion> = listOf(
            fractionPlaceholders,
            playersPlaceholders,
            outpostPlaceholders
        )
        if (Bukkit.getPluginManager().getPlugin(PLACEHOLDER_API_NAME) != null) {
            placeHolders.forEach { expansion ->
                if (!expansion.register()) {
                    logger.warning("Не удалось зарегистрировать placeholder с id = ${expansion.identifier}")
                }
            }
        } else {
            logger.warning("PlaceholderAPI недоступно")
        }
    }

}