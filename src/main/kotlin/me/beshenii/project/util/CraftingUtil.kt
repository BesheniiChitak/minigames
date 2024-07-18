@file:OptIn(ExperimentalContracts::class)

package me.beshenii.project.util

import me.beshenii.project.plugin
import me.beshenii.project.util.other.*
import net.kyori.adventure.text.Component.text
import org.bukkit.*
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.contracts.ExperimentalContracts

operator fun Inventory?.get(index: Int) {
    this?.contents?.get(index)
}

val craftingBlocks = mapOf(
    Location(Bukkit.getWorld("world"), -150.0, 28.0, -137.0) to listOf(
        "Normal",
        "Верстак",
        GameSound(Sound.BLOCK_BARREL_CLOSE, 2.0f, 1.0f),
        GameSound(soundType = Sound.ENTITY_VILLAGER_WORK_MASON, 1f, 1f)
    )
)

val recipesMap =
    mapOf("Normal" to listOfOffsets(Location(Bukkit.getWorld("world"), -149.0, -56.0, -83.0), 1.0, 0.0, 0.0, 1))

val doneRecipes = mutableMapOf<String, Collection<ItemStack>>()

fun reloadCrafts() {
    recipesMap.forEach { (key, locations) ->
        locations.forEach {
            val contents = (it.block.state as ShulkerBox).inventory.storageContents.clone()
            val listFromContents = contents.toList().filterNotNull()
            if (doneRecipes.contains(key)) doneRecipes[key] = doneRecipes[key]!! + listFromContents
            else doneRecipes[key] = listFromContents
        }
    }
}

object CraftingMenu {

    private val craftSlots = intArrayOf(10..16) + intArrayOf(19..25) + intArrayOf(28..34)

    fun open(player: Player, page: Int, name: Text, craftables: List<ItemStack>, openSound: GameSound, craftSound: GameSound) {
        player.playSound(openSound)
        InventoryMenu(player, 5, name + text(" [$page стр.]")) {
            for (slot in 0..44) {
                if (slot in craftSlots) setItem(
                    slot, doneItem(
                        Material.GRAY_DYE,
                        plain("Неизвестный или недоступный рецепт").color(0x555555),
                        plain("???").color(0x4a4a4a),
                        1
                    ), cancelClick = true
                )
                else {
                    if (slot == 36 && page > 1) {
                        addButton(36, previousPageItem) {
                            open(player, page - 1, name, craftables, openSound, craftSound)
                        }
                    } else setItem(slot, doneItem(Material.BLACK_STAINED_GLASS_PANE, plain(""), "", 1), cancelClick = true)
                }
            }
            for (index in page * 20 - 20 + (page - 1)..page * 21 + (page - 1)) {
                if (index >= craftables.size) return@InventoryMenu
                if (index > page * 20 + (page - 1)) {
                    addButton(44, nextPageItem) {
                        open(player, page + 1, name, craftables, openSound, craftSound)
                    }
                    return@InventoryMenu
                }
                val slot = craftSlots[( index ) % 21]
                addButton(slot, craftables[index], action = {
                    val itemsToDelete = mutableListOf<ItemStack>()
                    val exceed = mutableMapOf<Int, ItemStack>()
                    val clickedItem = this.clickedInventory?.contents?.get(this.slot) as ItemStack
                    val ingredients =
                        clickedItem.itemMeta.persistentDataContainer[NamespacedKey(
                            plugin, "ingredients"
                        ), PersistentDataType.STRING]?.stringToMap()!!
                    var ind = -1
                    for (item in player.inventory.contents) {
                        ind++
                        if (!item.isNullOrAir()) {
                            val tag = item.itemMeta.persistentDataContainer[NamespacedKey(
                                plugin, "material"
                            ), PersistentDataType.STRING]
                            if (ingredients.contains(tag)) {
                                val amount = item.amount
                                val ingredientAmount = ingredients[tag] as Int
                                if (amount >= ingredientAmount) {
                                    val exceedItem = item.clone()
                                    exceedItem.amount -= ingredientAmount
                                    if (ind != -1) exceed[ind] = exceedItem
                                    itemsToDelete.add(item)
                                    ingredients.remove(tag)
                                } else {
                                    itemsToDelete.add(item)
                                    ingredients[tag!!] = ingredients[tag]!! - amount
                                }
                                if (ingredients.isEmpty()) break
                            }
                        }
                    }
                    if (ingredients.isEmpty()) {
                        itemsToDelete.forEach {
                            player.inventory.removeItem(it)
                        }
                        exceed.forEach { (slot, exceedItem) ->
                            player.inventory.setItem(slot, exceedItem)
                        }
                        val result = clickedItem.itemMeta.persistentDataContainer[NamespacedKey(
                            plugin, "result"
                        ), PersistentDataType.STRING]?.stringToMap()
                        result?.forEach { (tagResult, resultAmount) ->
                            val itemResult = getItem(tagResult)
                            itemResult.amount = resultAmount
                            player.inventory.addItem(itemResult)
                            player.playSound(craftSound)
                        }
                    } else {
                        player.sendMessage(pluginMessage + plain("Недостаточно ресурсов для крафта.").color(0xffffff))
                        player.playSound(craftingUnsuccessfulSound)
                    }
                })
            }
        }.open()
    }
}