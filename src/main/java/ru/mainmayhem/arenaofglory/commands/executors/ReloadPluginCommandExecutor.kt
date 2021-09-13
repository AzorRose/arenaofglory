package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

class ReloadPluginCommandExecutor @Inject constructor(
    private val logger: PluginLogger,
    private val plugin: JavaPlugin
): BaseOpCommandExecutor() {

    override fun executeCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        logger.warning("Перезагружаем плагин...")
        sender.sendMessage("Перезагружаем плагин...")
        plugin.reloadConfig()
        plugin.onDisable()
        plugin.onEnable()
        sender.sendMessage("Перезагрузка прошла успешно")
        return true
    }

}