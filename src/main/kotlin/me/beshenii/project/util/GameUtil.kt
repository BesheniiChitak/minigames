package me.beshenii.project.util

import kotlinx.serialization.Serializable
import me.beshenii.project.bossbar
import me.beshenii.project.util.other.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.title.TitlePart
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
import org.bukkit.inventory.ItemStack
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
var game_players: MutableList<Player> = mutableListOf()

var max_players = 0
var min_players = 0

val defaultSettings = mutableMapOf("pillarsTimer" to "15", "pillarsEqual" to "false")
val settings = defaultSettings

val queue_join = item(Material.GRAY_DYE) {
    this.displayName(plain("Войти в очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "join"
}

val queue_exit = item(Material.LIME_DYE) {
    this.displayName(plain("Покинуть очередь"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "exit"
}

val spectate = item(Material.MAGENTA_DYE) {
    this.displayName(plain("Наблюдать"))
    this.persistentDataContainer[key("queue"), PersistentDataType.STRING] = "spectate"
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
                text("Недостаточно игроков для отсчёта. ") + text("[$size/$min_players] {max $max_players}").color(
                    0x878787
                )
            )
        } else {
            timer -= 1
            if (size == max_players && timer > 5) {
                timer = 5
            }
            server.sendActionBar(text("Игра начнётся через ") + text("$timer секунд.").color(0x91dceb))
            var pitch = 1f
            if (timer <= 10) {
                pitch += (10f-timer)/10f
            }
            Bukkit.getOnlinePlayers().forEach { player: Player ->
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, pitch)
                if (timer <= 10 && player !in queue_players && size < max_players) {
                    player.sendTitlePart(TitlePart.SUBTITLE, text("Ты не в очереди!").color(0xd68d8d))
                }
            }
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
        if (it !in queue_players) {
            it.inventory.setItem(4, spectate)
        }
    }

    cur_status = "running"

    val name = "game"

    Bukkit.unloadWorld(name, false)
    File(Bukkit.getWorldContainer(), name).deleteRecursively()

    val gameWorld = Bukkit.createWorld(WorldCreator(name).apply {
        type(WorldType.FLAT)
        generatorSettings("""{"layers":[],"biome":"the_void"}""")
    })

    game_players = queue_players
    val size = game_players.size

    when (cur_game) {
        "Столбы" -> {
            val center = Location(gameWorld, 0.0, 32.0, 0.0)
            val vector = Vector(0.6, 0.0, 0.0)
            for (i in 1..size) {
                vector.rotateAroundY(360.0 / size)
                val move = vector.clone().multiply(30 * size / 2)
                val pos = center.add(move)
                for (y in -64..32) {
                    pos.y = y.toDouble()
                    pos.block.type = Material.BEDROCK
                }
                pos.y = 33.5

                val player = game_players[i - 1]
                player.teleport(pos)
                player.gameMode = GameMode.SURVIVAL
            }
        }
    }

    gameHandler()
}

typealias M = Material
val disallowed = listOf(M.AIR, Material.VOID_AIR, Material.CAVE_AIR, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK)

val itemEntries = Material.entries.apply {
    this.toMutableList().removeAll(disallowed)
}

fun gameHandler() {
    when (cur_game) {
        "Столбы" -> {
            val needed = (settings["pillarsTimer"] ?: defaultSettings["pillarsTimer"])?.toIntOrNull() ?: 15
            val equal = settings["pillarsEqual"] ?: false

            var timer = needed/2
            runTaskTimer(1.seconds) {
                timer++
                bossbar.progress(timer/15f)
                bossbar.name(text("До следующего предмета"))
                if (timer == 15) {
                    timer = 0
                    var item = itemEntries.random()
                    game_players.forEach { player: Player ->
                        if (equal != "true") item = itemEntries.random()
                        val stack = ItemStack(item)
                        player.inventory.addItem(stack)
                        player.sendActionBar(text(" + ") + translatable(stack.translationKey()))
                        player.showBossBar(bossbar)
                    }
                }
                val size = game_players.size
                val server = Bukkit.getServer()
                if (size == 1) {
                    server.sendMessage(text("Победитель: ") + text(game_players[0].name).color(0xc091eb))
                } else if (size == 0) {
                    server.sendMessage(text("Все игроки умерли, нет победителя!").color(red))
                }
                if (size <= 1) {
                    gameEnd()
                    it.cancel()
                }
            }
        }
    }
}

fun gameEnd() {
    cur_status = null
    cur_game = null
    Bukkit.getOnlinePlayers().forEach { player: Player ->
        player.reset()
        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2f, 1f)
    }
}