package ru.mainmayhem.arenaofglory

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.ChooseFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.HelpCommandExecutor
import ru.mainmayhem.arenaofglory.data.dagger.components.DaggerAppComponent
import ru.mainmayhem.arenaofglory.data.dagger.modules.AppModule

class ArenaOfGlory: JavaPlugin() {

    override fun onEnable() {
        initDI()
        server.pluginManager.registerEvents(EventsListener(), this)
        initCommands()
    }

    override fun onDisable() {
        //todo close db connection
        DIHolder.clear()
    }

    private fun initDI(){
        DIHolder.setComponent(
            DaggerAppComponent.builder().appModule(
                AppModule(
                    plugin = this
                )
            ).build()
        )
    }

    private fun initCommands(){
        Commands.values().forEach {
            getCommand(it.cmdName)!!.setExecutor(
                when(it){
                    Commands.HELP -> HelpCommandExecutor()
                    Commands.CHOOSE_FRACTION -> ChooseFractionCommandExecutor()
                }
            )
        }
    }

}