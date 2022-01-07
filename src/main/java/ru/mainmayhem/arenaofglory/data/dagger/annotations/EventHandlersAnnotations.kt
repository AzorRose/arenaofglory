package ru.mainmayhem.arenaofglory.data.dagger.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ArenaKillingEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ArenaSuicideEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerQuitWRQueueHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerQuitArenaHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MoveToEnemyRespawnEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerEnteredOutpostEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerLeftOutpostEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TpToArenaEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TpToWaitingRoomEventHandlerInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TpToOutpostsEventHandlerInstance