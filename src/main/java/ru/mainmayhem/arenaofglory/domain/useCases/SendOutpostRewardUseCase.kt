package ru.mainmayhem.arenaofglory.domain.useCases

import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val COMMAND_PLAYER_NAME_KEY = "{player_name}"

/**
 * Класс для выполнения команд для раздачи награды за удержание аванпостов
 * Проходится по всем аванпостам и выполняет все команды на всех игроках фракции,
 * которая владеет данным аванпостом
 */
class SendOutpostRewardUseCase @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val dispatchers: CoroutineDispatchers,
    private val javaPlugin: JavaPlugin,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val logger: PluginLogger
) {

    suspend fun sendReward() {
        logger.info("Идет выдача награды за удержание аванпостов")
        withContext(dispatchers.default) {
            outpostsRepository.getCachedOutposts()
                .asSequence()
                .map { (_, data) -> data.outpost }
                .filter { outpost -> outpost.fractionId != null }
                .forEach { outpost ->
                    val fractionId = outpost.fractionId!!
                    val players = arenaPlayersRepository.getCachedPlayers()
                        .asSequence()
                        .filter { player -> player.fractionId == fractionId }
                        .filter { player -> javaPlugin.server.getPlayer(player.name)?.isOnline == true }
                        .map { player -> player.name }
                    withContext(dispatchers.main) {
                        players.forEach { player ->
                            outpost.rewardCommands.forEach { command ->
                                command.cmd performWith player
                            }
                        }
                    }
                }
        }
    }

    private infix fun String.performWith(player: String) {
        val command = replace(COMMAND_PLAYER_NAME_KEY, player)
        runCatching {
            val isExecuted = Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command
            )
            if (!isExecuted) {
                logger.warning(
                    message = "Команда {$command} не была выполнена",
                )
            }
        }.exceptionOrNull()?.let { error ->
            logger.error(
                className = "SendOutpostRewardUseCase",
                methodName = "String.performWith",
                message = "Ошибка при выполнении команды: $command",
                throwable = error
            )
        }
    }

}