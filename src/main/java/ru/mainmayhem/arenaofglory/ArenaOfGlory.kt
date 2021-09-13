package ru.mainmayhem.arenaofglory

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.*
import ru.mainmayhem.arenaofglory.data.dagger.components.DaggerAppComponent
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.InitDataUseCase
import ru.mainmayhem.arenaofglory.domain.useCases.KickAllArenaPLayersUseCase
import ru.mainmayhem.arenaofglory.jobs.MatchScheduleJob
import ru.mainmayhem.arenaofglory.placeholders.FractionPlaceholders
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ArenaOfGlory: JavaPlugin() {

    //commands
    @Inject internal lateinit var chooseFractionCommandExecutor: ChooseFractionCommandExecutor
    @Inject internal lateinit var changeFractionCommandExecutor: ChangeFractionCommandExecutor
    @Inject internal lateinit var enterWaitingRoomCommandExecutor: EnterWaitingRoomCommandExecutor
    @Inject internal lateinit var quitWaitingRoomCommandExecutor: QuitWaitingRoomCommandExecutor
    @Inject internal lateinit var reloadPluginCommandExecutor: ReloadPluginCommandExecutor

    @Inject internal lateinit var initDataUseCase: InitDataUseCase
    @Inject internal lateinit var kickAllArenaPLayersUseCase: KickAllArenaPLayersUseCase

    @Inject internal lateinit var coroutineScope: CoroutineScope

    @Inject internal lateinit var logger: PluginLogger

    @Inject internal lateinit var eventsListener: EventsListener

    @Inject internal lateinit var matchScheduleJob: MatchScheduleJob

    //placeholders
    @Inject internal lateinit var fractionPlaceholders: FractionPlaceholders

    override fun onEnable() {
        initDI()
        printCurrentServerDate()
        initPlaceholders()
        initData()
        server.pluginManager.registerEvents(eventsListener, this)
        initCommands()
        matchScheduleJob.start()
    }

    override fun onDisable() {
        kickAllArenaPLayersUseCase.doKickPlayers()
        matchScheduleJob.stop()
        coroutineScope.cancel(CancellationException())
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

    private fun printCurrentServerDate(){
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        logger.info("Серверное время: ${sdf.format(Date())}")
    }

    private fun initPlaceholders(){
        val placeHolders: List<PlaceholderExpansion> = listOf(
            fractionPlaceholders
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