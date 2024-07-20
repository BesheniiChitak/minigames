package me.beshenii.project.util

import me.beshenii.project.plugin
import me.beshenii.project.util.other.Text
import me.beshenii.project.util.other.plain
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

fun key(key: String) : NamespacedKey = NamespacedKey(plugin, key)