package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands

private const val ATTRIBUTES_SEPARATOR = " "

class HelpCommandExecutor: BaseOpCommandExecutor() {

    override fun executeCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        val result = StringBuilder()
        Commands.values().forEach { cmd ->
            val argsString = cmd.cmdAttributesName?.joinToString(ATTRIBUTES_SEPARATOR).orEmpty()
            result.append("${cmd.cmdName} $argsString - ${cmd.cmdDescription} \n")
        }
        sender.sendMessage(result.toString())
        return true
    }

}