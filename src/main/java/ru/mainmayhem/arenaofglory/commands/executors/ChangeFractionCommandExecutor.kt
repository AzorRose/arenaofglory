package ru.mainmayhem.arenaofglory.commands.executors

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.second

private const val COMMAND_ARGS_AMOUNT = 2

class ChangeFractionCommandExecutor @Inject constructor(
    private val database: PluginDatabase,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger,
    private val coroutineScope: CoroutineScope
): BaseOpCommandExecutor() {

    override fun executeCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (args.size != COMMAND_ARGS_AMOUNT) {
            sender.sendMessage("Некорректные аргументы")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с некорректными аргументами")
            return false
        }

        val fractionName = args.first()
        val playerName = args.second()

        if (!isFractionNameValid(fractionName)) {
            sender.sendMessage("Некорректное название фракции")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с некорректной фракцией")
            return false
        }

        if (!hasPlayerInFraction(playerName)) {
            sender.sendMessage("Игрок не принадлежит к фракции")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с игроком $playerName, который не принадлежит к фракции")
            return false
        }

        return updatePlayerFraction(playerName, fractionName, sender)

    }

    private fun updatePlayerFraction(
        playerName: String,
        fractionName: String,
        sender: CommandSender
    ): Boolean {
        val fractionId = fractionsRepository.getFractionByNameInEnglish(fractionName)?.id
        //асинхронно обновляем игрока в таблице
        //99,99% это будет успешно, поэтому считаем, что команда выполнена
        coroutineScope.launch {
            try {
                database.getArenaPlayersDao().updateFraction(
                    playerName = playerName,
                    newFractionId = fractionId!!
                )
                sender.sendMessage("Команда выполнена")
            } catch (t: Throwable) {
                logger.error(
                    className = "ChooseFractionCommandExecutor",
                    methodName = "insertNewPlayer",
                    throwable = t
                )
                sender.sendMessage("Ошибка при обновлении БД")
            }
        }
        return true
    }

    private fun isFractionNameValid(fractionName: String): Boolean {
        val fractions = fractionsRepository.getCachedFractions()
        fractions.forEach { fraciton ->
            if (fraciton.nameInEnglish == fractionName)
                return true
        }
        return false
    }

    private fun hasPlayerInFraction(playerName: String): Boolean {
        return arenaPlayersRepository.getCachedPlayerByName(playerName) != null
    }

}