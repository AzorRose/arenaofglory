package ru.mainmayhem.arenaofglory.jobs

import kotlinx.coroutines.*
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.Constants
import ru.mainmayhem.arenaofglory.data.CoroutineDispatchers
import ru.mainmayhem.arenaofglory.data.local.database.PluginDatabase
import ru.mainmayhem.arenaofglory.data.local.repositories.ArenaPlayersRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.FractionsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.local.repositories.PluginSettingsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import ru.mainmayhem.arenaofglory.domain.useCases.SendOutpostRewardUseCase
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta.Companion.MAX_PLACE_STATE
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceMeta.Companion.MIN_PLACE_STATE
import ru.mainmayhem.arenaofglory.places.ConquerablePlaceStatus
import ru.mainmayhem.arenaofglory.places.outposts.OutpostChatMessagesHelper
import ru.mainmayhem.arenaofglory.places.outposts.OutpostMeta
import ru.mainmayhem.arenaofglory.places.outposts.OutpostsHolder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutpostsJob @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val logger: PluginLogger,
    settingsRepository: PluginSettingsRepository,
    private val pluginDatabase: PluginDatabase,
    private val javaPlugin: JavaPlugin,
    private val outpostsRepository: OutpostsRepository,
    private val outpostsHolder: OutpostsHolder,
    private val arenaPlayersRepository: ArenaPlayersRepository,
    private val fractionsRepository: FractionsRepository,
    private val outpostChatMessagesHelper: OutpostChatMessagesHelper,
    private val sendOutpostRewardUseCase: SendOutpostRewardUseCase
) {

    private var job: Job? = null

    private val outpostConqueringDuration = settingsRepository.getSettings().outpostConqueringDuration

    //время последней раздачи награды
    private var lastRewardTimeMillis = System.currentTimeMillis()

    //период в минутах раз в который идет награда игроков за удержание аванпостов
    private val rewardTimePeriodMillis = 10 * 60 * 1000

    fun start(){
        if (job?.isActive == true)
            return
        lastRewardTimeMillis = System.currentTimeMillis()
        job = coroutineScope.launch(dispatchers.default) {
            try {
                while (isActive){
                    delay(1000)

                    if (System.currentTimeMillis() - lastRewardTimeMillis >= rewardTimePeriodMillis){
                        launch { sendOutpostRewardUseCase.sendReward() }
                        lastRewardTimeMillis = System.currentTimeMillis()
                    }

                    outpostsRepository.getCachedOutposts()
                        .forEach {outpost ->
                            outpostsHolder.getOutpostMeta(outpost.first.id)?.let {meta ->

                                if (meta.getStatus() !is ConquerablePlaceStatus.None && !meta.canBeCaptured()){
                                    return@let
                                }

                                val oldState = meta.getState()

                                //высчитываем новый процент захвата
                                val updated = getUpdatedState(
                                    oldState = oldState,
                                    status = meta.getStatus()
                                )

                                meta.updateState(updated)

                                val status = meta.getStatus()

                                //уведомляем фракцию о захвате ее аванпоста (если не делали это ранее)
                                if (status is ConquerablePlaceStatus.UnderAttack
                                    && updated >= Constants.OUTPOST_CAPTURE_PERCENT_NOTIFICATION
                                    && !meta.wasNotified
                                ){
                                    meta.defendingFractionId()?.let {
                                        sendAttackNotification(meta, getFractionName(status.attackingFractionId).orEmpty(), it)
                                    }
                                    meta.wasNotified = true
                                }

                                if (updated == MIN_PLACE_STATE || updated == MAX_PLACE_STATE){
                                    meta.wasNotified = false
                                }

                                when{
                                    status is ConquerablePlaceStatus.UnderAttack && updated == MAX_PLACE_STATE -> {
                                        val outpostName = GOLD.toString()
                                        val attackersName = RED.toString()
                                        val default = WHITE.toString()
                                        sendMessageToAllFractions(
                                            "Аванпост $outpostName${meta.getPlaceName()}$default теперь принадлежит " +
                                                    "фракции $attackersName${getFractionName(status.attackingFractionId)}"
                                        )
                                        meta.lastCaptureTime = Date().time
                                        meta.updateState(MIN_PLACE_STATE)
                                        launch { changeFraction(meta.getPlaceId(), status.attackingFractionId) }
                                        return@let
                                    }
                                    status !is ConquerablePlaceStatus.None && updated != oldState -> {
                                        val default = GOLD.toString()
                                        val percent = RED.toString()
                                        val formattedState = meta.getFormattedState()
                                        outpostChatMessagesHelper.sendMessageToAttackers(meta, "$default Захват аванпоста: $percent$formattedState%")
                                        outpostChatMessagesHelper.sendMessageToDefenders(meta, "$default Потеря аванпоста: $percent$formattedState%")
                                    }
                                }
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

    private fun sendMessageToAllFractions(message: String){
        arenaPlayersRepository.getCachedPlayers().forEach {
            javaPlugin.server.getPlayer(it.name)?.sendMessage(message)
        }
    }

    private fun sendAttackNotification(
        meta: OutpostMeta,
        attackersName: String,
        fractionId: Long
    ){
        val attackersFont = RED.toString()
        val outpostNameFont = GOLD.toString()
        val defendersFont = LIGHT_PURPLE.toString()
        val default = WHITE.toString()
        val defendersName = getFractionName(fractionId)
        val msg = "$attackersFont$attackersName$default осаждает аванпост $outpostNameFont${meta.getPlaceName()}$default!! " +
                "$defendersFont$defendersName$default немедленно пришлите своих воинов и организуйте защиту!!"
        sendMessageToFraction(msg, fractionId)
    }

    private suspend fun changeFraction(outpostId: Long, fractionId: Long){
        try {
            pluginDatabase.getOutpostsDao().changeOwner(outpostId, fractionId)
        } catch (t: Throwable){
            logger.error(
                className = "OutpostsJob",
                methodName = "changeFraction",
                throwable = t
            )
        }
    }

    private fun getUpdatedState(oldState: Double, status: ConquerablePlaceStatus): Double {

        fun getDiff(playersDelta: Int): Double {
            return MAX_PLACE_STATE / outpostConqueringDuration * playersDelta
        }

        return when(status) {
            is ConquerablePlaceStatus.UnderAttack -> {
                val diff = getDiff(status.attackingPlayersDelta)
                val state = oldState + diff
                if (state > MAX_PLACE_STATE) return MAX_PLACE_STATE else state
            }
            is ConquerablePlaceStatus.Stalemate -> oldState
            is ConquerablePlaceStatus.Defending -> {
                val diff = getDiff(status.defendingPlayersDelta)
                val state = oldState - diff
                if (state < MIN_PLACE_STATE) MIN_PLACE_STATE else state
            }
            is ConquerablePlaceStatus.None -> {
                val diff = getDiff(1)
                val state = oldState - diff
                if (state < MIN_PLACE_STATE) MIN_PLACE_STATE else state
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