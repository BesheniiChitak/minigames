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

const val red = 0xeb91a8
const val green = 0x9ddb8c
const val light_blue = 0xd9fff0

