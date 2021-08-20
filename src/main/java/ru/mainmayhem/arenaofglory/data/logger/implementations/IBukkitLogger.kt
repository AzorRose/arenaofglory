package ru.mainmayhem.arenaofglory.data.logger.implementations

import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.logging.Level
import java.util.logging.Logger

class IBukkitLogger(
    private val logger: Logger
): PluginLogger {

    private val prefix = "[ArenaOfGlory]"

    override fun info(message: String) = logger.info("$prefix $message")

    override fun warning(message: String) = logger.warning("$prefix $message")

    override fun error(className: String, methodName: String, throwable: Throwable) {
        logger.log(Level.INFO, "$prefix Ошибка в классе $className в методе $methodName", throwable)
    }

    override fun error(message: String, className: String, methodName: String, throwable: Throwable) {
        warning("$prefix $message")
        error(className, methodName, throwable)
    }

}