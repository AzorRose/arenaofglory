package ru.mainmayhem.arenaofglory.data

object Constants {

    //где сохраняются конфиги и прочее
    const val PLUGIN_META_FOLDER_NAME = "ArenaOfGlory"

    //время ожидания в комнате ожидания перед началом матча в минутах
    const val ARENA_QUEUE_DELAY_IN_MINUTES = 5

    //время ожидания на арене перед началом матча в секундах
    const val ARENA_START_MATCH_DELAY_IN_SECONDS = 10

    //продолжительность матча в минутах
    const val MATCH_TIME_IN_MINUTES = 15

    //fixme название мира лучше все же вынести в БД
    const val WORLD_NAME = "world"

    //кол-во очков, выдаваемое фракции за убийство
    const val FRACTION_KILL_PONTS = 100

}