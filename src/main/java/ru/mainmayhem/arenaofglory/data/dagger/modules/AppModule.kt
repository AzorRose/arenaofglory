package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.logger.implementations.IBukkitLogger
import javax.inject.Singleton

@Module
class AppModule(
    private val plugin: JavaPlugin
) {

    @Provides
    fun getLogger(): PluginLogger = IBukkitLogger(plugin.server.logger)

    @Singleton
    @Provides
    fun getPluginClass(): JavaPlugin = plugin

}