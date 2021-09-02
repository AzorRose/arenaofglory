package ru.mainmayhem.arenaofglory.domain.events

import org.bukkit.event.Event

abstract class BaseEventHandler<T: Event>: EventHandler<T> {

    private var nextHandler: EventHandler<T>? = null

    override fun setNext(eventHandler: EventHandler<T>?) {
        nextHandler = eventHandler
    }

    override fun handle(event: T) {
        nextHandler?.handle(event)
    }

}