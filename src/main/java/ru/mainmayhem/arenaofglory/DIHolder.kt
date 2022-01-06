package ru.mainmayhem.arenaofglory

import ru.mainmayhem.arenaofglory.data.dagger.components.AppComponent

object DIHolder {

    private var component: AppComponent? = null

    fun setComponent(component: AppComponent) {
        this.component = component
    }

    fun getComponent(): AppComponent = component!!

    fun clear() {
        component = null
    }

}