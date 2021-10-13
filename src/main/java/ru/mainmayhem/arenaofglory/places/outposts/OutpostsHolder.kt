package ru.mainmayhem.arenaofglory.places.outposts

import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutpostsHolder @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin
) {

    private val outpostsMeta: List<OutpostMeta> by lazy {
        outpostsRepository.getCachedOutposts()
            .map {
                OutpostMeta(
                    outpostId = it.first.id,
                    outpostName = it.first.name,
                    outpostsRepository = outpostsRepository,
                    javaPlugin = javaPlugin
                )
            }
    }

    fun getOutpostMeta(id: Long): OutpostMeta?{
        return outpostsMeta.find { it.getPlaceId() == id }
    }

    @Synchronized
    fun addPlayer(arenaPlayer: ArenaPlayer, outpostId: Long){
        val meta = getOutpostMeta(outpostId)
        if (meta == null){
            logger.warning("Не найден аванпост с id = $outpostId")
            return
        }
        meta.addPlayer(arenaPlayer)
        logger.info("Игрок ${arenaPlayer.name} зашел на территорию аванпоста ${meta.getPlaceName()}")
        logger.info("Новый статус аванпоста = ${meta.getStatus()}")
    }

    @Synchronized
    fun removePlayer(arenaPlayer: ArenaPlayer, outpostId: Long){
        val meta = getOutpostMeta(outpostId)
        if (meta == null){
            logger.warning("Не найден аванпост с id = $outpostId")
            return
        }
        meta.removePlayer(arenaPlayer.id, arenaPlayer.fractionId)
        logger.info("Игрок ${arenaPlayer.name} вышел с территории аванпоста ${meta.getPlaceName()}")
        logger.info("Новый статус аванпоста = ${meta.getStatus()}")
    }

}