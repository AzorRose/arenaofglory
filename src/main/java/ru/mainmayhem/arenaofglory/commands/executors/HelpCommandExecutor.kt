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
            if (it == Commands.CHOOSE_FRACTION){
                //todo убрать хардкод названий фракций
                result.append("${it.cmdName} ${it.cmdAttributeName.orEmpty()} ${it.cmdDescription} (kapella, procion) \n")
            } else {
                result.append("${it.cmdName} ${it.cmdAttributeName.orEmpty()} ${it.cmdDescription} \n")
            }
        }
        sender.sendMessage(result.toString())
        return true
    }
}