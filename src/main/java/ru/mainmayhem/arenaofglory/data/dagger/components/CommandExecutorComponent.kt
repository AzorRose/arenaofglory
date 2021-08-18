package ru.mainmayhem.arenaofglory.data.dagger.components

import dagger.Subcomponent
import ru.mainmayhem.arenaofglory.commands.executors.ChooseFractionCommandExecutor

@Subcomponent
interface CommandExecutorComponent {
    fun injectChooseFractionExecutor(executor: ChooseFractionCommandExecutor)
}