package ru.mainmayhem.arenaofglory

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.ChangeFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.ChooseFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.EnterWaitingRoomCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.HelpCommandExecutor
import ru.mainmayhem.arenaofglory.data.dagger.components.DaggerAppComponent
import ru.mainmayhem.arenaofglory.data.dagger.modules.AppModule
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.InitDataUseCase
import javax.inject.Inject

class ArenaOfGlory: JavaPlugin() {

    @Inject internal lateinit var chooseFractionCommandExecutor: ChooseFractionCommandExecutor
    @Inject internal lateinit var changeFractionCommandExecutor: ChangeFractionCommandExecutor
    @Inject internal lateinit var enterWaitingRoomCommandExecutor: EnterWaitingRoomCommandExecutor
    @Inject internal lateinit var initDataUseCase: InitDataUseCase
    @Inject internal lateinit var coroutineScope: CoroutineScope
    @Inject internal lateinit var logger: PluginLogger
    @Inject internal lateinit var eventsListener: EventsListener

    override fun onEnable() {
        initDI()
        initData()
        server.pluginManager.registerEvents(eventsListener, this)
        initCommands()
    }

    override fun onDisable() {
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
            DaggerAppComponent.builder().appModule(
                AppModule(
                    plugin = this
                )
            ).build()
        )
        DIHolder.getComponent().injectArenaOfGloryClass(this)
    }

    private fun initCommands(){
        Commands.values().forEach {
            getCommand(it.cmdName)!!.setExecutor(
                when(it){
                    Commands.HELP -> HelpCommandExecutor()
                    Commands.CHOOSE_FRACTION -> chooseFractionCommandExecutor
                    Commands.CHANGE_FRACTION -> changeFractionCommandExecutor
                    Commands.ENTER_WAITING_ROOM -> enterWaitingRoomCommandExecutor
                }
            )
        }
    }

}