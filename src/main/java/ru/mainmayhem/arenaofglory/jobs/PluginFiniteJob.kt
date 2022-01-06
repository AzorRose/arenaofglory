package ru.mainmayhem.arenaofglory.jobs

import java.util.concurrent.TimeUnit

interface PluginFiniteJob: PluginJob {

    fun getLeftTime(): Long

    fun getTimerStepTimeUnit(): TimeUnit

}