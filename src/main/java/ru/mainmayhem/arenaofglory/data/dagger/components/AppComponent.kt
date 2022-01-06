package ru.mainmayhem.arenaofglory.data.dagger.components

import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.ArenaOfGlory
import ru.mainmayhem.arenaofglory.data.dagger.modules.AppModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.EventHandlerModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.JobModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.RepositoryModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.StorageModule

@Singleton
@Component(
    modules = [
        AppModule::class,
        StorageModule::class,
        RepositoryModule::class,
        JobModule::class,
        EventHandlerModule::class
    ]
)
interface AppComponent {

    fun injectArenaOfGloryClass(arenaOfGlory: ArenaOfGlory)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance javaPlugin: JavaPlugin): AppComponent
    }

}