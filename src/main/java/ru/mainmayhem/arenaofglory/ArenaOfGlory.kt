package ru.mainmayhem.arenaofglory

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.*
import ru.mainmayhem.arenaofglory.data.dagger.components.DaggerAppComponent
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.InitDataUseCase
import ru.mainmayhem.arenaofglory.domain.useCases.KickAllArenaPLayersUseCase
import ru.mainmayhem.arenaofglory.jobs.MatchScheduleJob
import ru.mainmayhem.arenaofglory.jobs.OutpostsJob
import ru.mainmayhem.arenaofglory.placeholders.FractionPlaceholders
import ru.mainmayhem.arenaofglory.placeholders.OutpostPlaceholders
import ru.mainmayhem.arenaofglory.placeholders.PlayersPlaceholders
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ArenaOfGlory: JavaPlugin() {

    //commands
    @Inject lateinit var chooseFractionCommandExecutor: ChooseFractionCommandExecutor
    @Inject lateinit var changeFractionCommandExecutor: ChangeFractionCommandExecutor
    @Inject lateinit var enterWaitingRoomCommandExecutor: EnterWaitingRoomCommandExecutor
    @Inject lateinit var quitWaitingRoomCommandExecutor: QuitWaitingRoomCommandExecutor
    @Inject lateinit var reloadPluginCommandExecutor: ReloadPluginCommandExecutor

    @Inject lateinit var initDataUseCase: InitDataUseCase
    @Inject lateinit var kickAllArenaPLayersUseCase: KickAllArenaPLayersUseCase

    @Inject lateinit var coroutineScope: CoroutineScope

    @Inject lateinit var logger: PluginLogger

    @Inject lateinit var eventsListener: EventsListener

    @Inject lateinit var matchScheduleJob: MatchScheduleJob
    @Inject lateinit var outpostsJob: OutpostsJob

    //placeholders
    @Inject lateinit var fractionPlaceholders: FractionPlaceholders
    @Inject lateinit var playersPlaceholders: PlayersPlaceholders
    @Inject lateinit var outpostPlaceholders: OutpostPlaceholders

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

    private fun initData(){
        coroutineScope.launch {
            kotlin.runCatching {
                initDataUseCase.init()
            }.exceptionOrNull()?.let {
                logger.error(
                    methodName = "initData",
                    className = "ArenaOfGlory",
                    throwable = it
                )
            }
        }
    }

    private fun initDI(){
        DIHolder.setComponent(
            DaggerAppComponent.factory().create(this)
        )
        DIHolder.getComponent().injectArenaOfGloryClass(this)
    }

    private fun initCommands(){
        Commands.values().forEach {
            getCommand(it.cmdName)!!.setExecutor(
                when(it){
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

    private fun printCurrentServerDate(){
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        logger.info("Серверное время: ${sdf.format(Date())}")
    }

    private fun initPlaceholders(){
        val placeHolders: List<PlaceholderExpansion> = listOf(
            fractionPlaceholders,
            playersPlaceholders,
            outpostPlaceholders
        )
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeHolders.forEach {
                if (!it.register()){
                    logger.warning("Не удалось зарегистрировать placeholder с id = ${it.identifier}")
                }
            }
        } else {
            logger.warning("PlaceholderAPI недоступно")
        }
    }

}