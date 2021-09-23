package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutpostsJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val logger: PluginLogger,
    settingsRepository: PluginSettingsRepository,
    private val javaPlugin: JavaPlugin,
    private val outpostsRepository: OutpostsRepository,
    private val outpostsHolder: OutpostsHolder,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository
) {

    private var job: Job? = null

    private val outpostConqueringDuration = settingsRepository.getSettings().outpostConqueringDuration

    fun start(){
        if (job?.isActive == true)
            return
        job = coroutineScope.launch {
            try {
                while (isActive){
                    outpostsRepository.getCachedOutposts()
                        .forEach {outpost ->
                            outpostsHolder.getOutpostMeta(outpost.first.id)?.let {meta ->
                                if (meta.getStatus() is ConquerablePlaceStatus.UnderAttack && !meta.canBeCaptured()){
                                    meta.sendMessageToAttackers(
                                        "Данный аванпост находится под защитой, захватить его можно будет через ${meta.getProtectedModeDuration()} мин"
                                    )
                                    return@let
                                }
                                val updated = getUpdatedState(
                                    oldState = meta.getState(),
                                    status = meta.getStatus()
                                )
                                val status = meta.getStatus()
                                if (status is ConquerablePlaceStatus.UnderAttack
                                    && updated >= Constants.OUTPOST_CAPTURE_PERCENT_NOTIFICATION
                                    && !meta.wasNotified
                                ){
                                    meta.defendingFractionId()?.let {
                                        sendMessageToFraction(
                                            message = "Фракция ${getFractionName(status.attackingFractionId)} ведет захват аванпоста ${meta.getPlaceName()}",
                                            fractionId = it
                                        )
                                    }
                                    meta.wasNotified = true
                                }
                                if (updated == 0 || updated == 100){
                                    meta.wasNotified = false
                                }
                                if (updated == 100){
                                    meta.sendMessageToAttackers("Аванпост захвачен")
                                    meta.sendMessageToDefenders("Аванпост потерян")
                                    meta.lastCaptureTime = Date().time
                                } else{
                                    meta.sendMessageToAttackers("Захват аванпоста: $updated%")
                                    meta.sendMessageToDefenders("Потеря аванпоста: $updated%")
                                }
                                meta.updateState(updated)
                            }
                        }
                }
            } catch (t: Throwable){
                if (t !is CancellationException) {
                    logger.error(
                        className = "OutpostsJob",
                        methodName = "job",
                        throwable = t
                    )
                }
            }
        }
    }

    fun stop(){
        job?.cancel(CancellationException())
        job = null
    }

    private fun ConquerablePlaceMeta.sendMessageToAttackers(message: String){
        getPlayers().filter {
            it.key != defendingFractionId()
        }.values.forEach {
            it.forEach { player ->
                javaPlugin.server.getPlayer(player.name)?.sendMessage(message)
            }
        }
    }

    private fun ConquerablePlaceMeta.sendMessageToDefenders(message: String){
        val fractionId = defendingFractionId() ?: return
        getPlayers()[fractionId]?.forEach {
            javaPlugin.server.getPlayer(it.name)?.sendMessage(message)
        }
    }

    private fun getUpdatedState(oldState: Int, status: ConquerablePlaceStatus): Int{

        fun getDiff(playersDelta: Int): Int{
            return 100 / outpostConqueringDuration * playersDelta
        }

        return when(status){
            is ConquerablePlaceStatus.UnderAttack -> {
                val diff = getDiff(status.attackingPlayersDelta)
                val state = oldState + diff
                if (state > 100) return 100 else state
            }
            is ConquerablePlaceStatus.Stalemate -> oldState
            is ConquerablePlaceStatus.Defending -> {
                val diff = getDiff(status.defendingPlayersDelta)
                val state = oldState - diff
                if (state < 0) 0 else state
            }
            is ConquerablePlaceStatus.None -> {
                val diff = getDiff(1)
                val state = oldState - diff
                if (state < 0) 0 else state
            }
        }
    }

    private fun getFractionName(fractionId: Long): String?{
        return fractionsRepository.getCachedFractions().find { it.id == fractionId }?.name
    }

    private fun sendMessageToFraction(message: String, fractionId: Long){
        arenaPlayersRepository
            .getCachedPlayers()
            .filter { it.fractionId == fractionId }
            .forEach {
                javaPlugin.server.getPlayer(it.name)?.sendMessage(message)
            }
    }

}