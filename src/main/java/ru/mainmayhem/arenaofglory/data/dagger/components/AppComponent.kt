package ru.mainmayhem.arenaofglory.data.dagger.components

import dagger.Component
import ru.mainmayhem.arenaofglory.data.dagger.modules.AppModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.RepositoryModule
import ru.mainmayhem.arenaofglory.data.dagger.modules.StorageModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, StorageModule::class, RepositoryModule::class])
interface AppComponent {
    fun createRepositoryComponent(): RepositoryComponent
    fun createCmdExecutorComponent(): CommandExecutorComponent
}