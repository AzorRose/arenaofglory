package ru.mainmayhem.arenaofglory.data.local

data class DbConfig(
    val url: String,
    val driver: String,
    val user: String?,
    val password: String?
)
