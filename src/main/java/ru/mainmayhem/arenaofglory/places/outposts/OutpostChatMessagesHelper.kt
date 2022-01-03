package ru.mainmayhem.arenaofglory.places.outposts

import org.bukkit.plugin.java.JavaPlugin
import javax.inject.Inject

class OutpostChatMessagesHelper @Inject constructor(
    private val javaPlugin: JavaPlugin
) {

    private var attackersLastMessageTime: Long? = null
    private var defendersLastMessageTime: Long? = null
    private val deltaInSeconds = 5

    fun sendMessageToDefenders(outpostMeta: OutpostMeta, message: String){
        if (checkDelta(defendersLastMessageTime)){
            outpostMeta.sendMessageToDefenders(message, javaPlugin)
            defendersLastMessageTime = System.currentTimeMillis()
        }
    }

    fun sendMessageToAttackers(outpostMeta: OutpostMeta, message: String){
        if (checkDelta(attackersLastMessageTime)){
            outpostMeta.sendMessageToAttackers(message, javaPlugin)
            attackersLastMessageTime = System.currentTimeMillis()
        }
    }

    private fun checkDelta(checkedMillis: Long?): Boolean{
        val curr = System.currentTimeMillis()
        return checkedMillis == null || curr - checkedMillis >= deltaInSeconds * 1000
    }

}