package ru.mainmayhem.arenaofglory.data

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatchers(
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val default: CoroutineDispatcher,
    val unconfirmed: CoroutineDispatcher
)