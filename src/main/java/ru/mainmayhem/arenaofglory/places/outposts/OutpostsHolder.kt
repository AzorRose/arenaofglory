package ru.mainmayhem.arenaofglory.places.outposts

import javax.inject.Inject
import javax.inject.Singleton
import org.bukkit.plugin.java.JavaPlugin
import ru.mainmayhem.arenaofglory.data.entities.ArenaPlayer
import ru.mainmayhem.arenaofglory.data.local.repositories.OutpostsRepository
import ru.mainmayhem.arenaofglory.data.logger.PluginLogger

@Singleton
class OutpostsHolder @Inject constructor(
    private val outpostsRepository: OutpostsRepository,
    private val logger: PluginLogger,
    private val javaPlugin: JavaPlugin
) {

    private val outpostsMeta: Map<Long, OutpostMeta> by lazy {
        outpostsRepository.getCachedOutposts()
            .map { (outpostId, data) ->
                outpostId to OutpostMeta(
                    outpostId = outpostId,
                    outpostName = data.outpost.name,
                    outpostsRepository = outpostsRepository,
                    javaPlugin = javaPlugin
                )
            }
            .toMap()
    }

    fun getOutpostMeta(id: Long): OutpostMeta? {
        return outpostsMeta[id]
    }

    fun addPlayer(arenaPlayer: ArenaPlayer, outpostId: Long) {
        val meta = getOutpostMeta(outpostId)
        if (meta == null) {
            logger.warning("Не найден аванпост с id = $outpostId")
            return
        }
        meta.addPlayer(arenaPlayer)
    }

    fun removePlayer(arenaPlayer: ArenaPlayer, outpostId: Long) {
        val meta = getOutpostMeta(outpostId)
        if (meta == null) {
            logger.warning("Не найден аванпост с id = $outpostId")
            return
        }
        meta.removePlayer(arenaPlayer.id, arenaPlayer.fractionId)
    }

}