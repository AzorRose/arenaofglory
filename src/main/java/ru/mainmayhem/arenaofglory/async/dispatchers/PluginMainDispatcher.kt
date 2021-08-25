package ru.mainmayhem.arenaofglory.async.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PluginMainDispatcher @Inject constructor(
    private val javaPlugin: JavaPlugin
): CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().callSyncMethod(javaPlugin){
            block.run()
        }
    }

}