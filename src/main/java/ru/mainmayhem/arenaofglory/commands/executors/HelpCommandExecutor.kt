package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands

class HelpCommandExecutor: BaseOpCommandExecutor() {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!super.onCommand(sender, command, label, args)){
            return false
        }

        val result = StringBuilder()
        Commands.values().forEach {
            result.append("${it.cmdName} ${it.cmdAttributesName.orEmpty()} - ${it.cmdDescription} \n")
        }
        sender.sendMessage(result.toString())
        return true
    }

}