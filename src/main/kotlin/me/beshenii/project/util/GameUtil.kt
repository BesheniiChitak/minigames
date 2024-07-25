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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
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

var stop = false

val defaultSettings = mutableMapOf("pillarsTimer" to "8", "pillarsEqual" to "false")
val settings = defaultSettings.clone()

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
    stop = false
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

        if (stop) {
            server.sendMessage(text("Очередь была остановлена"))
            gameEnd()
            it.cancel()
        }

        val size = queue_players.size
        if (size < min_players) {
            timer = max
            server.sendActionBar(
                text("Недостаточно игроков для отсчёта. ") + text("[$size/$max_players] {min $min_players}").color(
                    0x878787
                )
            )
        } else {
            timer -= 1
            if (size == max_players && timer > 5) {
                timer = 5
            }
            server.sendActionBar(
                text("Игра начнётся через ") + text("$timer секунд. ").color(0x91dceb) + text("[$size/$max_players]").color(
                    0x878787
                )
            )
            var pitch = 1f
            if (timer <= 10) {
                pitch += (10f - timer) / 10f
            }
            Bukkit.getOnlinePlayers().forEach { player: Player ->
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, pitch)
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
        } else {
            it.isInvulnerable = false
        }
    }

    cur_status = "running"

    val name = "game"

    Bukkit.unloadWorld(name, false)
    File(Bukkit.getWorldContainer(), name).deleteRecursively()

    val gameWorld = Bukkit.createWorld(WorldCreator(name).apply {
        type(WorldType.FLAT)
        generatorSettings("""{"layers":[],"biome":"the_void"}""")
    }) ?: return

    gameWorld.difficulty = Difficulty.HARD

    gameWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
    gameWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
    gameWorld.setGameRule(GameRule.DO_INSOMNIA, false)
    gameWorld.setGameRule(GameRule.KEEP_INVENTORY, true)

    gameWorld.worldBorder.size = 150.0

    game_players = queue_players
    val size = game_players.size

    when (cur_game) {
        "Столбы" -> {
            val rotate = Math.toRadians(360.0 / size)
            val center = Location(gameWorld, 0.5, 32.5, 0.5)
            val vector = Vector(7.0 + size, 0.0, 0.0)
            for (i in 0..<size) {
                vector.rotateAroundY(rotate)
                val pos = center.clone().add(vector)
                for (y in -64..32) {
                    pos.y = y.toDouble()
                    pos.block.type = Material.BEDROCK
                }
                pos.y = 33.5

                val player = game_players[i]

                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 ,10))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 20 ,-10))

                player.isInvulnerable = true

                runTaskLater(25) {
                    player.isInvulnerable = false
                }

                player.teleport(pos)

                player.gameMode = GameMode.SURVIVAL
                player.showBossBar(bossbar)
            }
        }
    }

    gameHandler()
}

fun gameHandler() {

    val gameWorld = Bukkit.getWorld("game") ?: return

    when (cur_game) {
        "Столбы" -> {
            val needed = (settings["pillarsTimer"] ?: defaultSettings["pillarsTimer"])?.toFloatOrNull() ?: 15f
            val equal = settings["pillarsEqual"] ?: false

            var timer = 0f
            runTaskTimer(0.25.seconds) {

                gameWorld.worldBorder.size -= 0.025

                if (stop) {
                    Bukkit.getServer().sendMessage(text("Игра была остановлена"))
                    gameEnd()
                    it.cancel()
                }

                timer += 0.25f
                bossbar.progress(timer / needed)
                bossbar.name(text("До следующего предмета"))
                if (timer >= needed) {
                    timer = 0f
                    var item = itemEntries.random()
                    game_players.forEach { player: Player ->
                        if (equal != "true") item = itemEntries.random()
                        while (item.blockTranslationKey == null && rand(1, 6) == 1) item = itemEntries.random()
                        val stack = ItemStack(item)
                        player.inventory.addItem(stack)
                        player.sendActionBar(text(" + ") + translatable(stack.translationKey()))
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
    runTaskLater(2) {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            player.reset()
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2f, 1f)
        }
    }
}

val disallowed = listOf(
    Material.COMMAND_BLOCK,
    Material.CHAIN_COMMAND_BLOCK,
    Material.REPEATING_COMMAND_BLOCK,
    Material.JIGSAW,
    Material.COMMAND_BLOCK_MINECART,
    Material.DEBUG_STICK,
    Material.ENCHANTED_BOOK,
    Material.WRITTEN_BOOK,
    Material.STRUCTURE_VOID
)

lateinit var itemEntries: MutableList<Material>

fun initialize() {
    itemEntries = Material.entries.toMutableList()
    itemEntries.removeAll(disallowed)

    val world = Bukkit.getWorld("world") ?: return

    val iterator = itemEntries.iterator()

    while (iterator.hasNext()) {
        val item: Material = iterator.next()

        if (!item.isEnabledByFeature(world) || item.isLegacy || item.isEmpty || item.itemTranslationKey == null) {
            iterator.remove()
        }
    }
}