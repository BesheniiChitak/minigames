package me.beshenii.project.util

import kotlinx.serialization.Serializable
import me.beshenii.project.util.other.plain
import me.beshenii.project.util.other.runTaskTimer
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import kotlin.time.Duration.Companion.seconds

val Location.blockPos: BlockPos get() = BlockPos(x.toInt(), y.toInt(), z.toInt())
val Player.nms: ServerPlayer get() = (this as CraftPlayer).handle
val Block.nms: BlockState get() = (this as CraftBlock).nms
val World.nms: ServerLevel get() = (this as CraftWorld).handle

fun Location.setBlockDestruction(source: Player, progress: Int, showToEveryone: Boolean = false) {
    val dec = progress - 1
    val pos = blockPos
    val players = if (showToEveryone) world.players
    else listOf(source)
    players.forEach { player ->
        player.nms.connection.send(
            ClientboundBlockDestructionPacket(
                if (player == source) 0 else source.entityId,
                pos,
                dec
            )
        )
    }
}

var cur_game: String? = null
var cur_status: String? = null

val queue_join = item(Material.GRAY_DYE) {
    this.displayName(plain("Войти в очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "join"
}

val queue_exit = item(Material.LIME_DYE) {
    this.displayName(plain("Покинуть очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "leave"
}

@Serializable
class PlayerSave

fun hostQueue() {
    val players = Bukkit.getOnlinePlayers()
    players.forEach { player: Player ->
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 10f, 1.2f)
        player.sendMessage("Начинается игра \"$cur_game\"! Для входа в очередь нажмите ПКМ, держа краситель.")
        player.inventory.setItem(4, queue_join)
    }

    cur_status = "queue"
    var timer = 50.0

    runTaskTimer(0.5.seconds) {

    }

}

