package ru.mainmayhem.arenaofglory.domain.useCases

import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject

/**
 * Класс для выполнения команд для раздачи награды за удержание аванпостов
 * Проходится по всем аванпостам и выполняет все команды на всех игроках фракции,
 * которая владеет данным аванпостом
 */
class SendOutpostRewardUseCase @Inject constructor (
    private val outpostsRepository: OutpostsRepository,
    private val dispatchers: CoroutineDispatchers,
    private val javaPlugin: JavaPlugin,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val logger: PluginLogger
) {

    suspend fun sendReward(){
        logger.info("Идет выдача награды за удержание аванпостов")
        withContext(dispatchers.default){
            outpostsRepository.getCachedOutposts()
                .map { it.first }
                .filter { it.fractionId != null }
                .forEach { outpost ->
                    val fractionId = outpost.fractionId!!
                    val players = arenaPlayersRepository.getCachedPlayers()
                        .filter { it.fractionId == fractionId }
                        .filter { javaPlugin.server.getPlayer(it.name)?.isOnline == true }
                        .map { it.name }
                    withContext(dispatchers.main){
                        players.forEach { player ->
                            outpost.rewardCommands.forEach { command ->
                                command.cmd performWith player
                            }
                        }
                    }
                }
        }
    }

    private infix fun String.performWith(player: String){
        val command = replace("{player_name}", player)
        kotlin.runCatching {
            val isExecuted = Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command
            )
            if (!isExecuted){
                logger.warning(
                    message = "Команда {$command} не была выполнена",
                )
            }
        }.exceptionOrNull()?.let {
            logger.error(
                className = "SendOutpostRewardUseCase",
                methodName = "String.performWith",
                message = "Ошибка при выполнении команды: $command",
                throwable = it
            )
        }
    }

}