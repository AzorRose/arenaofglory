package ru.mainmayhem.arenaofglory.data.logger

interface PluginLogger {

    fun info(message: String)

    fun warning(message: String)

    fun error(message: String, className: String, methodName: String, throwable: Throwable)

    fun error(className: String, methodName: String, throwable: Throwable)

}