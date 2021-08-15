package ru.mainmayhem.arenaofglory.commands

//todo доставать строки из гредла
enum class Commands(
    val cmdName: String,
    val cmdAttributeName: String?,
    val cmdDescription: String
) {

    CHOOSE_FRACTION(
        cmdName = "arenachoosefraction",
        cmdAttributeName = "<fraction name>",
        cmdDescription = "Выбор фракции"
    ),

    HELP(
        cmdName = "arenahelp",
        cmdAttributeName = null,
        cmdDescription = "Выводит все команды"
    )

}