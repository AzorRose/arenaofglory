package ru.mainmayhem.arenaofglory.commands.executors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.mainmayhem.arenaofglory.commands.Commands
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

class ChangeFractionCommandExecutor @Inject constructor(
    private val database: PluginDatabase,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val logger: PluginLogger,
    private val coroutineScope: CoroutineScope
): BaseOpCommandExecutor() {

    override fun executeCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args.size != 2){
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

        if (!hasPlayerInFraction(playerId)){
            sender.sendMessage("Игрок не принадлежит к фракции")
            logger.info("${sender.name} выполнил команду ${Commands.CHOOSE_FRACTION} с игроком $playerId, который не принадлежит к фракции")
            return false
        }

        return updatePlayerFraction(playerId, fractionName)

    }

    private fun updatePlayerFraction(playerId: String, fractionName: String): Boolean{
        val fractionId = fractionsRepository.getCachedFractions().find { it.nameInEnglish == fractionName }?.id
        //асинхронно обновляем игрока в таблице
        //99,99% это будет успешно, поэтому считаем, что команда выполнена
        coroutineScope.launch {
            try {
                val player = database.getArenaPlayersDao().getByPlayerId(playerId)
                database.getArenaPlayersDao().update(player!!.copy(fractionId = fractionId!!))
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