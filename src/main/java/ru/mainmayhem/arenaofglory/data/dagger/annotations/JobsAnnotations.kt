package ru.mainmayhem.arenaofglory.data.dagger.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EmptyTeamJobInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MatchScheduleJobInstance