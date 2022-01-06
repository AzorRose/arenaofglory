package ru.mainmayhem.arenaofglory.data.dagger.modules

import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import ru.mainmayhem.arenaofglory.data.dagger.annotations.EmptyTeamJobInstance
import ru.mainmayhem.arenaofglory.data.dagger.annotations.MatchScheduleJobInstance
import ru.mainmayhem.arenaofglory.jobs.EmptyTeamJob
import ru.mainmayhem.arenaofglory.jobs.MatchScheduleJob
import ru.mainmayhem.arenaofglory.jobs.PluginFiniteJob
import ru.mainmayhem.arenaofglory.jobs.PluginJob

@Module
abstract class JobModule {

    @Singleton
    @Binds
    @EmptyTeamJobInstance
    abstract fun getEmptyTeamJob(impl: EmptyTeamJob): PluginFiniteJob

    @Singleton
    @Binds
    @MatchScheduleJobInstance
    abstract fun getMatchScheduleJob(impl: MatchScheduleJob): PluginJob

}