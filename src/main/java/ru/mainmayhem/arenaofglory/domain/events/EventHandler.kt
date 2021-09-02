package ru.mainmayhem.arenaofglory.domain.events

import org.bukkit.event.Event

interface EventHandler<T: Event> {

    fun handle(event: T)

    fun setNext(eventHandler: EventHandler<T>?)

}