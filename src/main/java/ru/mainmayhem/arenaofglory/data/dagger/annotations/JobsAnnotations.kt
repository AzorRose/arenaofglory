package ru.mainmayhem.arenaofglory.data.dagger.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EmptyTeamJobInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MatchScheduleJobInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MatchJobInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OutpostsJobInstance

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StartMatchDelayJobInstance