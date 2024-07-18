package me.beshenii.project.util

import me.beshenii.project.plugin
import me.beshenii.project.util.other.*
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import kotlin.contracts.ExperimentalContracts

fun item(material: Material, amount: Int = 1) = ItemStack(material, amount)
fun item(material: Material, amount: Int = 1, editMeta: ItemMeta.() -> Unit) =
    ItemStack(material, amount).apply { editMeta { it.editMeta() } }

fun item(item: ItemStack, editMeta: ItemMeta.() -> Unit) = ItemStack(item).apply { editMeta { it.editMeta() } }

fun ItemStack.tagItem(tags: Map<String, *>): ItemStack {
    return this.tagPlacement(tags)
}

val nextPageItem = item(Material.SPECTRAL_ARROW, 1) {
    this.displayName(plain("Следующая страница"))
}

val previousPageItem = item(Material.SPECTRAL_ARROW, 1) {
    this.displayName(plain("Предыдущая страница"))
}

fun ItemStack.tagPlacement(tags: Map<String, *>): ItemStack {
    tags.forEach { (key, value) ->
        this.editMeta {
            when (value) {
                is Int -> it.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.INTEGER, value)
                is Double -> it.persistentDataContainer.set(
                    NamespacedKey(plugin, key),
                    PersistentDataType.DOUBLE,
                    value
                )

                is String -> it.persistentDataContainer.set(
                    NamespacedKey(plugin, key),
                    PersistentDataType.STRING,
                    value
                )

                is Map<*, *> -> it.persistentDataContainer.set(
                    NamespacedKey(plugin, key),
                    PersistentDataType.STRING,
                    value.mapToString()
                )
            }
        }
    }
    return this
}

@ExperimentalContracts
fun ItemStack?.isNullOrAir(): Boolean {
    kotlin.contracts.contract {
        returns(false) implies (this@isNullOrAir is ItemStack)
    }
    return this == null || type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR
}

fun doneItem(material: Material, lore: Text, name: String, modeldata: Int): ItemStack {
    return ItemStack(material).lore(lore).name(plain(name)).data(modeldata)
}

fun doneItem(material: Material, lore: Text, name: Text, modeldata: Int): ItemStack {
    return ItemStack(material).lore(lore).name(name).data(modeldata)
}

fun ItemStack.lore(lore: Text): ItemStack {
    if (lore != plain("")) this.lore(listOf(lore))
    return this
}

fun ItemStack.name(name: Text): ItemStack {
    this.editMeta {
        it.displayName(name)
    }
    return this
}

fun ItemStack.data(modeldata: Int): ItemStack {
    this.editMeta {
        it.setCustomModelData(modeldata)
    }
    return this
}

var allItems = mapOf(
    "mithril_ingot" to doneItem(Material.IRON_INGOT, epicLore * materialLore, "Слиток мифрила", 1),
    "orichalcum_ingot" to doneItem(Material.IRON_INGOT, epicLore * materialLore, "Слиток орихалка", 2),
    "tungsten_ingot" to doneItem(Material.IRON_INGOT, epicLore * materialLore, "Слиток вольфрама", 3),
    "silver_ingot" to doneItem(Material.IRON_INGOT, rareLore * materialLore, "Слиток серебра", 4),
    "galvorn_ingot" to doneItem(Material.IRON_INGOT, mythicLore * materialLore, "Слиток галворна", 5),
    "meteorite_iron_ingot" to doneItem(
        Material.IRON_INGOT,
        legendaryLore * materialLore,
        "Слиток метеоритного железа",
        6
    ),
    "cobalt_ingot" to doneItem(Material.IRON_INGOT, legendaryLore * materialLore, "Слиток кобальта", 7),
    "lead_ingot" to doneItem(Material.IRON_INGOT, uncommonLore * materialLore, "Слиток свинца", 8)
)

fun getItem(id: String): ItemStack {
    val item: ItemStack
    val loreID: String
    val customItem = allItems[id.lowercase()]?.clone()
    if (customItem != null) {
        item = customItem
        loreID = "mmo"
    } else {
        item = try {
            ItemStack(Material.valueOf(id.uppercase()))
        } catch(error: Exception) {
            item(ItemStack(Material.DIRT)) {
                displayName(text("бляяяять такого предмета не существует (${error})"))
            }
        }
        loreID = "vanilla"
    }
    val lore = item.lore() ?: mutableListOf()
    lore.add(plain("$loreID:${id.lowercase()}").color(0x555555))
    item.lore(lore)
    return item.tagItem(mapOf("material" to id))
}

fun key(key: String) : NamespacedKey = NamespacedKey(plugin, key)