package ru.mainmayhem.arenaofglory.data

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatchers(
    val io: CoroutineDispatcher,
    val default: CoroutineDispatcher,
    val unconfirmed: CoroutineDispatcher
)