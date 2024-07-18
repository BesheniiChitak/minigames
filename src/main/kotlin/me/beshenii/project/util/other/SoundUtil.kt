package me.beshenii.project.util.other

import org.bukkit.Sound
import org.bukkit.entity.Player

class GameSound(
    val soundType: Sound,
    val pitch: Float = 1f,
    val volume: Float = 1f
)

fun Player.playSound(sound: GameSound) {
    this.playSound(this, sound.soundType, sound.pitch, sound.volume)
}

val craftingUnsuccessfulSound = GameSound(soundType = Sound.ENTITY_VILLAGER_NO, 1f, 1f)