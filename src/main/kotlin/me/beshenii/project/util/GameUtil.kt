package me.beshenii.project.util

import kotlinx.serialization.Serializable
import me.beshenii.project.util.other.color
import me.beshenii.project.util.other.plain
import me.beshenii.project.util.other.plus
import me.beshenii.project.util.other.runTaskTimer
import net.kyori.adventure.text.Component.text
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
import org.bukkit.util.Vector
import java.io.File
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
var queue_players: MutableList<Player> = mutableListOf()

var max_players = 0
var min_players = 0

val queue_join = item(Material.GRAY_DYE) {
    this.displayName(plain("Войти в очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "join"
}

val queue_exit = item(Material.LIME_DYE) {
    this.displayName(plain("Покинуть очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "exit"
}

@Serializable
class PlayerSave

fun hostQueue() {
    val players = Bukkit.getOnlinePlayers()
    queue_players = mutableListOf()
    players.forEach { player: Player ->
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 10f, 1.2f)
        player.sendMessage("Начинается игра \"$cur_game\"! Для входа в очередь нажмите ПКМ, держа краситель.")
        player.inventory.setItem(4, queue_join)
    }

    cur_status = "queue"
    val max = 40
    var timer = max

    val server = Bukkit.getServer()

    runTaskTimer(1.seconds) {
        val size = queue_players.size
        if (size < min_players) {
            timer = max
            server.sendActionBar(
                text("Недостаточно игроков для отсчёта. ") + text("[$size/$min_players]").color(
                    0x878787
                )
            )
        } else {
            timer -= 1
            if (size == max_players && timer > 5) {
                timer = 5
            }
            server.sendActionBar(text("Игра начнётся через ") + text("$timer секунд.").color(0x91dceb))
            if (timer <= 0) {
                gameRun()
                it.cancel()
            }
        }

    }

}

fun gameRun() {

    Bukkit.getOnlinePlayers().forEach {
        it.inventory.clear()
    }

    cur_status = "running"

    val name = "game"

    Bukkit.unloadWorld(name, false)
    File(Bukkit.getWorldContainer(), name).deleteRecursively()

    val gameWorld = Bukkit.createWorld(WorldCreator(name).apply {
        type(WorldType.FLAT)
        generatorSettings("""{"layers":[],"biome":"the_void"}""")
    })

    val size = queue_players.size

    when (cur_game) {
        "Столбы" -> {
            val center = Location(gameWorld, 0.0, 32.0, 0.0)
            val vector = Vector(0.6, 0.0, 0.0)
            for (i in 1..size) {
                vector.rotateAroundY(360.0 / size)
                val move = vector.clone().multiply(20 * size / 2)
                val pos = center.add(move)
                for (y in -64..32) {
                    pos.y = y.toDouble()
                    pos.block.type = Material.BEDROCK
                }
                pos.y = 33.5

                val player = queue_players[i - 1]
                player.teleport(pos)
                player.gameMode = GameMode.SURVIVAL
            }
        }
    }

}

