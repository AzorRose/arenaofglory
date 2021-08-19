package ru.mainmayhem.arenaofglory.commands

/**
 * Все команды плагина
 * Чтобы добавить команду, нужно:
 * -создать команду в Commands
 * -продублировать название и описание команды в файле plugin.yml
 * -создать новый executor в пакете commands.executors
 * -добавить executor в классе ArenafOfGlory
 */
enum class Commands(
    val cmdName: String,
    val cmdAttributesName: List<String>?,
    val cmdDescription: String
) {

    CHOOSE_FRACTION(
        cmdName = "arenachoosefraction",
        cmdAttributesName = listOf("<fraction name>", "<player id>"),
        cmdDescription = "Выбор фракции"
    ),

    CHANGE_FRACTION(
        cmdName = "arenachangefraction",
        cmdAttributesName = listOf("<new fraction name>", "<player id>"),
        cmdDescription = "Смена фракции"
    ),

    HELP(
        cmdName = "arenahelp",
        cmdAttributesName = null,
        cmdDescription = "Выводит все команды"
    )

}