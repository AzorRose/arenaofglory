package ru.mainmayhem.arenaofglory

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.commands.executors.ChooseFractionCommandExecutor
import ru.mainmayhem.arenaofglory.commands.executors.HelpCommandExecutor

class ArenaOfGlory: JavaPlugin() {

    override fun onEnable() {
        //todo init di
        server.pluginManager.registerEvents(EventsListener(), this)
        initCommands()
    }

    override fun onDisable() {
        //todo close db connection
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