package ru.mainmayhem.arenaofglory.data

object Constants {

    //где сохраняются конфиги и прочее
    const val PLUGIN_META_FOLDER_NAME = "ArenaOfGlory"

    //время ожидания на арене перед началом матча в секундах
    const val ARENA_START_MATCH_DELAY_IN_SECONDS = 10

    //Продолжительность задержки до автовина, если команда полностью вышла с арены
    const val EMPTY_TEAM_DELAY_IN_SECONDS = 60

    //fixme название мира лучше все же вынести в БД
    const val WORLD_NAME = "world"

    //кол-во очков, выдаваемое фракции за убийство
    const val FRACTION_KILL_PONTS = 100

    //кол-во процентов захвата, после которого команда обороны получит уведомление в чате о захвате
    const val OUTPOST_CAPTURE_PERCENT_NOTIFICATION = 15

    //сколько нужно ждать в минутах после последнего захвата аванпоста, чтобы захватить по новой
    const val OUTPOST_CAPTURE_DELAY = 60

    //время
    const val MILLIS_IN_MINUTE = 60_000

}