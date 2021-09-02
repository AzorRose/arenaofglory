package ru.mainmayhem.arenaofglory.commands.executors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.*
import javax.inject.Inject

/**
 * Команда для выбора фракции
 * Выполняется только если игрок не принадлежит ни к одной фракции
 * usage:<название команды> <название фракции на английском из табл fractions> <id игрока>
 */

class ChooseFractionCommandExecutor @Inject constructor(
    private val database: PluginDatabase,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val coroutineScope: CoroutineScope,
    private val plugin: JavaPlugin,
    private val logger: PluginLogger
): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.lowercase() != Commands.CHOOSE_FRACTION.cmdName) return false

        if (args.argumentsNotCorrect()){
            sender.sendMessage("Некорректные аргументы")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с некорректными аргументами")
            return false
        }

        val fractionName = args.first()
        val playerId = args[1]

        if (!isFractionNameValid(fractionName)){
            sender.sendMessage("Некорректное название фракции")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с некорректной фракцией")
            return false
        }

        if (hasPlayerInFraction(playerId)){
            sender.sendMessage("Игрок принадлежит к фракции")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с игроком $playerId, который принадлежит к фракции")
            return false
        }

        return insertNewPlayer(playerId, fractionName)
    }

    private fun Array<out String>.argumentsNotCorrect(): Boolean{
        return size != 2
    }

    private fun insertNewPlayer(playerId: String, fractionName: String): Boolean{
        val playerName = plugin.server.getPlayer(UUID.fromString(playerId))?.displayName
        if (playerName == null){
            logger.error(
                message = "Игрок с id = $playerId не найден",
                className = "ChooseFractionCommandExecutor",
                methodName = "insertNewPlayer",
                throwable = NullPointerException()
            )
            return false
        }
        val fractionId = fractionsRepository.getCachedFractions().find { it.nameInEnglish == fractionName }?.id
        if (fractionId == null){
            logger.error(
                message = "Фракция с названием = $fractionName не найдена",
                className = "ChooseFractionCommandExecutor",
                methodName = "insertNewPlayer",
                throwable = NullPointerException()
            )
            return false
        }
        //асинхронно добавляем нового игрока в таблицу
        //99,99% это будет успешно, поэтому считаем, что команда выполнена
        coroutineScope.launch {
            try {
                database.getArenaPlayersDao().insert(
                    ArenaPlayer(
                        id = playerId,
                        name = playerName,
                        fractionId = fractionId
                    )
                )
            } catch (t: Throwable){
                logger.error(
                    className = "ChooseFractionCommandExecutor",
                    methodName = "insertNewPlayer",
                    throwable = t
                )
            }
        }
        return true
    }

    private fun isFractionNameValid(fractionName: String): Boolean{
        val fractions = fractionsRepository.getCachedFractions()
        fractions.forEach {
            if (it.nameInEnglish == fractionName)
                return true
        }
        return false
    }

    private fun hasPlayerInFraction(playerId: String): Boolean{
        val players = arenaPlayersRepository.getCachedPlayers()
        return players.find { it.id == playerId } != null
    }

}