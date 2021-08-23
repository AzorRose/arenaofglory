package ru.mainmayhem.arenaofglory.data.dagger.components

import dagger.BindsInstance
import dagger.Component
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.ArenaOfGlory
import ru.mainmayhem.arenaofglory.data.dagger.modules.AppModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.RepositoryModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.StorageModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, StorageModule::class, RepositoryModule::class])
interface AppComponent {

    fun injectArenaOfGloryClass(arenaOfGlory: ArenaOfGlory)

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance javaPlugin: JavaPlugin): AppComponent
    }

}