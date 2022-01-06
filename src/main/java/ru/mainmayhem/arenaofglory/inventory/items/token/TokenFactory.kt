package ru.mainmayhem.arenaofglory.inventory.items.token

import javax.inject.Inject
import org.bukkit.ChatColor.RESET
import org.bukkit.ChatColor.WHITE
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

private const val TOKEN_ENCHANTMENT_LEVEL = 6

/**
 * Класс для создания жетонов
 */
class TokenFactory @Inject constructor() {

    fun getTokens(amount: Int): ItemStack {
        val token = ItemStack(Material.MAGMA_CREAM, amount)
        val loreFontSettings = RESET.toString() + WHITE.toString()
        val meta = token.itemMeta?.apply {
            setDisplayName(RESET.toString() + "Жетон войны")
            lore = listOf(
                loreFontSettings + "Жетон, полученный за участие",
                loreFontSettings + "в кровопролитной битве"
            )
            addEnchant(Enchantment.LOOT_BONUS_BLOCKS, TOKEN_ENCHANTMENT_LEVEL, true)
        }
        token.itemMeta = meta
        return token
    }

}