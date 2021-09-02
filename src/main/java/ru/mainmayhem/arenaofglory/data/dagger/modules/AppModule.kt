package ru.mainmayhem.arenaofglory.data.dagger.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.async.dispatchers.PluginMainDispatcher
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.data.logger.implementations.IBukkitLogger
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun getAppCoroutineScope(d: CoroutineDispatchers): CoroutineScope =
        CoroutineScope(d.io + Job())

    @Provides
    fun getDispatchers(m: PluginMainDispatcher): CoroutineDispatchers{
        return CoroutineDispatchers(
            io = Dispatchers.IO,
            default = Dispatchers.Default,
            unconfirmed = Dispatchers.Unconfined,
            main = m
        )
    }

    @Provides
    fun getLogger(plugin: JavaPlugin): PluginLogger = IBukkitLogger(plugin.server.logger)

    @Provides
    @Singleton
    fun getMoshi(): Moshi =
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

}