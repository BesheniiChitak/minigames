package me.beshenii.project.util

import me.beshenii.project.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.contracts.ExperimentalContracts

fun item(material: Material, amount: Int = 1) = ItemStack(material, amount)
fun item(material: Material, amount: Int = 1, editMeta: ItemMeta.() -> Unit) =
    ItemStack(material, amount).apply { editMeta { it.editMeta() } }

fun item(item: ItemStack, editMeta: ItemMeta.() -> Unit) = ItemStack(item).apply { editMeta { it.editMeta() } }

@ExperimentalContracts
fun ItemStack?.isNullOrAir(): Boolean {
    kotlin.contracts.contract {
        returns(false) implies (this@isNullOrAir is ItemStack)
    }
    return this == null || type.isEmpty
}

fun key(key: String) : NamespacedKey = NamespacedKey(plugin, key)