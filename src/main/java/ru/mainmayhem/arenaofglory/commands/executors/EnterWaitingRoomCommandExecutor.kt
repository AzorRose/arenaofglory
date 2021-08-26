package ru.mainmayhem.arenaofglory.commands.executors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.TeleportToWaitingRoomUseCase
import java.util.*
import javax.inject.Inject

/**
 * Команда для входа в комнату ожидания
 * Выполняется только если игрок принадлежит к одной из фракции
 * usage:<название команды> <id игрока>
 */

class EnterWaitingRoomCommandExecutor @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val pluginLogger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val teleportToWaitingRoomUseCase: TeleportToWaitingRoomUseCase,
    private val coroutineScope: CoroutineScope
): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.isEmpty()){
            sender.sendMessage("Укажите в аргументах id игрока")
            return false
        }

        val playerId = args.first()

        if (arenaPlayersRepository.getCachedPlayerById(playerId) == null){
            javaPlugin.server.getPlayer(UUID.fromString(playerId))?.sendMessage("Вы не принадлежите к фракции")
            return false
        }

        //тяжелая команда с кучей логики, решил вызывать асинхронно
        coroutineScope.launch {
            try {
                teleportToWaitingRoomUseCase.teleport(playerId)
            } catch (e: Throwable){
                pluginLogger.error("EnterWaitingRoomCommandExecutor", "onCommand", e)
            }
        }

        return true
    }

}