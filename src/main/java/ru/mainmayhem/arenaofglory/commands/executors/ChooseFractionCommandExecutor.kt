package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands

class ChooseFractionCommandExecutor: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.lowercase() != Commands.CHOOSE_FRACTION.cmdName) return false
        //todo выбор фракции
        sender.sendMessage("fraction test")
        return true
    }
}