package ru.mainmayhem.arenaofglory.commands.executors

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaQueueRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

/**
 * Команда для выхода из комнаты ожидания
 * Удаляет из очереди и переносит игрока на спавн
 * usage:<название команды> <имя игрока>
 */
class QuitWaitingRoomCommandExecutor @Inject constructor(
    private val arenaQueueRepository: ArenaQueueRepository,
    private val javaPlugin: JavaPlugin,
    private val logger: PluginLogger
): BaseOpCommandExecutor() {

    override fun executeCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            sender.sendMessage("Укажите в аргументах id или имя игрока")
            return false
        }

        val playerName = args.first()

        val player = javaPlugin.server.getPlayer(playerName)

        if (player == null){
            sender.sendMessage("Игрок с именем $playerName не найден")
            return false
        }

        arenaQueueRepository.remove(player.uniqueId.toString())

        javaPlugin.server.getWorld(Constants.WORLD_NAME)?.let {
            logger.info("Переносим игрока в локацию: ${it.spawnLocation}")
            player.teleport(it.spawnLocation)
        }

        return true

    }

}