package ru.mainmayhem.arenaofglory.commands.executors

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.WaitingRoomScheduleHelper
import ru.mainmayhem.arenaofglory.domain.useCases.TeleportToWaitingRoomUseCase

/**
 * Команда для входа в комнату ожидания
 * Выполняется только если игрок принадлежит к одной из фракции
 * usage:<название команды> <имя игрока>
 */
class EnterWaitingRoomCommandExecutor @Inject constructor(
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val pluginLogger: PluginLogger,
    private val javaPlugin: JavaPlugin,
    private val teleportToWaitingRoomUseCase: TeleportToWaitingRoomUseCase,
    private val coroutineScope: CoroutineScope,
    private val waitingRoomScheduleHelper: WaitingRoomScheduleHelper
): BaseOpCommandExecutor() {

    override fun executeCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.isEmpty()) {
            sender.sendMessage("Укажите в аргументах имя игрока")
            return false
        }

        val playerName = args.first()

        if (arenaPlayersRepository.getCachedPlayerByName(playerName) == null) {
            sendMessageToPlayer(playerName, "Вы не принадлежите к фракции")
            return false
        }

        if (!waitingRoomScheduleHelper.isWaitingRoomOpened()) {
            sendMessageToPlayer(playerName, "Комната ожидания закрыта")
            return false
        }

        val playerId = javaPlugin.server.getPlayer(playerName)?.uniqueId?.toString()

        if (playerId == null) {
            sendMessageToPlayer(playerName, "Ошибка при выполнении команды")
            pluginLogger.error(
                className = "EnterWaitingRoomCommandExecutor",
                methodName = "executeCommand",
                throwable = NullPointerException(),
                message = "Игрок с именем $playerName не найден на сервере"
            )
            return false
        }

        //тяжелая команда с кучей логики, решил вызывать асинхронно
        coroutineScope.launch {
            try {
                teleportToWaitingRoomUseCase.teleport(playerId)
            } catch (e: Throwable) {
                pluginLogger.error("EnterWaitingRoomCommandExecutor", "onCommand", e)
            }
        }

        return true
    }

    private fun sendMessageToPlayer(playerName: String, message: String) {
        javaPlugin.server.getPlayer(playerName)?.sendMessage(message)
    }

}