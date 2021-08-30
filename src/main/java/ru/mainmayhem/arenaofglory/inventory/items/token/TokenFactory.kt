package ru.mainmayhem.arenaofglory.inventory.items.token

import org.bukkit.ChatColor.RESET
import org.bukkit.ChatColor.WHITE
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import javax.inject.Inject

/**
 * Класс для создания жетонов
 */
class TokenFactory @Inject constructor() {

    fun getTokens(amount: Int): ItemStack{
        val token = ItemStack(Material.MAGMA_CREAM, amount)
        val meta = token.itemMeta?.apply {
            setDisplayName(RESET.toString() + "Жетон войны")
            lore = listOf(RESET.toString() + WHITE.toString() + "Жетон, полученный за участие \nв кровопролитной битве")
            addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 6, true)
        }
        token.itemMeta = meta
        return token
    }

}