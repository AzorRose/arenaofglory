package ru.mainmayhem.arenaofglory.data.logger.implementations

import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import java.util.logging.Logger

class IBukkitLogger(
    private val logger: Logger
): PluginLogger {

    override fun info(message: String) = logger.info(message)

    override fun warning(message: String) = logger.warning(message)

    override fun error(className: String, methodName: String, throwable: Throwable) {
        logger.throwing(className, methodName, throwable)
    }

    override fun error(message: String, className: String, methodName: String, throwable: Throwable) {
        warning(message)
        error(className, methodName, throwable)
    }

}