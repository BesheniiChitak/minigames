package me.beshenii.project.util

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player

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

@Serializable
class PlayerSave

fun hostQueue() {

}

