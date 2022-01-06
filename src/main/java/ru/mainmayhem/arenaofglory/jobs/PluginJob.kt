package ru.mainmayhem.arenaofglory.jobs

interface PluginJob {

    fun start()

    fun stop()

    fun isActive(): Boolean

}