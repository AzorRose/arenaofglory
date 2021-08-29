package ru.mainmayhem.arenaofglory.inventory.items.token

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
            setDisplayName("Жетон войны")
            lore = listOf("Жетон, полученный за участие в кровопролитной битве")
            addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 6, true)
        }
        token.itemMeta = meta
        return token
    }

}