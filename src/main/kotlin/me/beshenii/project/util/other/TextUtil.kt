package me.beshenii.project.util.other

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

typealias Text = Component

operator fun Text.plus(other: Text): Component = this.append(other)
operator fun Text.times(other: Text): Component = this.append(other).color(this.color())
operator fun Text.minus(other: Text): Component = this.append(other).color(other.color())

fun Component.color(color: Int) = this.color(TextColor.color(color))
fun TextComponent.color(color: Int) = this.color(TextColor.color(color))
fun plain(text: String): Component = text(text).decoration(TextDecoration.ITALIC, false)
fun plain(text: Text): Component = text.decoration(TextDecoration.ITALIC, false)

val commonLore = plain("ОБЫЧНЫЙ").decorate(TextDecoration.BOLD).color(0xd6d6d6)
val uncommonLore = plain("НЕОБЫЧНЫЙ").decorate(TextDecoration.BOLD).color(0x81fc83)
val rareLore = plain("РЕДКИЙ").decorate(TextDecoration.BOLD).color(0x4786e6)
val epicLore = plain("ЭПИЧЕСКИЙ").decorate(TextDecoration.BOLD).color(0xc96ffc)
val legendaryLore = plain("ЛЕГЕНДАРНЫЙ").decorate(TextDecoration.BOLD).color(0xf8ff75)
val mythicLore = plain("МИФИЧЕСКИЙ").decorate(TextDecoration.BOLD).color(0xff759c)
val secretLore = plain("СЕКРЕТНЫЙ").decorate(TextDecoration.BOLD).color(0x1c1c1c)

val materialLore = plain(" МАТЕРИАЛ")

val pluginPrefix = plain("PʀᴏᴊᴇᴄᴛMMO").color(0xd3b5ff)
val pluginMessage = plain("[") - pluginPrefix * plain("]") + plain(" » ").color(0xAAAAAA)

