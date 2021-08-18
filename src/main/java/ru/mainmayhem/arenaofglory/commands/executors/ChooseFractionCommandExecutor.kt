package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.DIHolder
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import javax.inject.Inject

class ChooseFractionCommandExecutor: CommandExecutor {

    @Inject
    internal lateinit var database: PluginDatabase

    @Inject
    internal lateinit var arenaPlayersRepository: ArenaPlayersRepository

    init {
        DIHolder.getComponent().createCmdExecutorComponent().injectChooseFractionExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.lowercase() != Commands.CHOOSE_FRACTION.cmdName) return false
        //0) проверяем правильность аргментов
        //1) проверяем принаджежит ли игрок к какой-то фракции
        //2) делаем запись
        sender.sendMessage("fraction test")
        return true
    }
}