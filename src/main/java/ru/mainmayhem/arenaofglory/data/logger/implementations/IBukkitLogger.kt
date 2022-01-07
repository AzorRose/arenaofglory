package ru.mainmayhem.arenaofglory.data.logger.implementations

import java.util.logging.Level
import java.util.logging.Logger
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

private const val PREFIX = "[ArenaOfGlory]"

class IBukkitLogger(
    private val logger: Logger
): PluginLogger {

    override fun info(message: String) = logger.info("$PREFIX $message")

    override fun warning(message: String) = logger.warning("$PREFIX $message")

    override fun error(className: String, methodName: String, throwable: Throwable) {
        logger.log(Level.INFO, "$PREFIX Ошибка в классе $className в методе $methodName", throwable)
    }

    override fun error(message: String, className: String, methodName: String, throwable: Throwable) {
        warning("$PREFIX $message")
        error(className, methodName, throwable)
    }

}