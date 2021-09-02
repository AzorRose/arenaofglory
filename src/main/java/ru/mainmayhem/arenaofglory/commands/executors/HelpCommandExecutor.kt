package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands

class HelpCommandExecutor: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.lowercase() != Commands.HELP.cmdName) return false
        val result = StringBuilder()
        Commands.values().forEach {
            result.append("${it.cmdName} ${it.cmdAttributesName.orEmpty()} - ${it.cmdDescription} \n")
        }
        sender.sendMessage(result.toString())
        return true
    }

}