package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.logger.implementations.IBukkitLogger
import javax.inject.Singleton

@Module
class AppModule(
    private val plugin: JavaPlugin
) {

    @Provides
    @Singleton
    fun getAppCoroutineScope(d: CoroutineDispatchers): CoroutineScope =
        CoroutineScope(d.io + Job())

    @Provides
    fun getDispatchers(): CoroutineDispatchers{
        return CoroutineDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            default = Dispatchers.Default,
            unconfirmed = Dispatchers.Unconfined
        )
    }

    @Provides
    fun getLogger(plugin: JavaPlugin): PluginLogger = IBukkitLogger(plugin.server.logger)

    @Singleton
    @Provides
    fun getPluginClass(): JavaPlugin = plugin

}