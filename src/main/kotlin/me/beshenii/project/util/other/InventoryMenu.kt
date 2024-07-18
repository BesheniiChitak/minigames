package me.beshenii.project.util.other

import me.beshenii.project.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

fun Listener.unregister() = HandlerList.unregisterAll(this)
fun Listener.register() = Bukkit.getPluginManager().registerEvents(this, plugin)

class InventoryMenu(
    private val player: Player,
    rows: Int,
    title: Text,
    private val builder: InventoryMenu.() -> Unit = {}
) : InventoryHolder, Listener {

    private var closed = false
    private val _inventory = Bukkit.createInventory(this, rows * 9, title)
    private val objects = HashMap<Int, InventoryObject>()
    private var closeHandler: InventoryCloseEvent.() -> Unit = {}
    private var updateHandler: () -> Unit = {}

    override fun getInventory(): Inventory = _inventory

    fun onClose(action: InventoryCloseEvent.() -> Unit) {
        closeHandler = action
    }

    fun updater(action: () -> Unit) {
        runTaskTimer(0.05.seconds) {
            if (closed) it.cancel()
            else action()
        }
    }

    fun update(action: () -> Unit) {
        updateHandler = action
    }

    fun update() {
        updateHandler()
    }

    fun addButton(slot: Int,  item: ItemStack, action: InventoryClickEvent.() -> Unit): InventoryMenu = apply {
        objects[slot] = InventoryButton(action)
        inventory.setItem(slot, item)
    }


    fun setItem(slot: Int, item: ItemStack, cancelClick: Boolean) {
        objects[slot] = InventoryItem(cancelClick)
        inventory.setItem(slot, item)
    }

    fun setItems(slots: IntArray, item: ItemStack, cancelClick: Boolean) {
        slots.forEach { slot ->
            setItem(slot, item, cancelClick)
        }
    }

    fun setItems(slots: IntRange, item: ItemStack, cancelClick: Boolean) {
        slots.forEach { slot ->
            setItem(slot, item, cancelClick)
        }
    }

    fun open(): InventoryMenu {
        builder(this)
        register()
        player.openInventory(inventory)
        return this
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return
        if (clickedInventory.holder != this) return

        val slot = event.slot

        objects[slot].apply {
            when (this) {
                is InventoryButton -> {
                    event.isCancelled = true
                    clickHandler(event)
                }
                is InventoryItem -> {
                    event.isCancelled = cancelClick
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder != this) return
        if (event.reason != InventoryCloseEvent.Reason.PLUGIN)
            closeHandler(event)
        closed = true
        inventory.clear()
        objects.clear()
        unregister()
    }
}

data class InventoryButton(
    val clickHandler: InventoryClickEvent.() -> Unit
) : InventoryObject

data class InventoryItem(
    val cancelClick: Boolean
) : InventoryObject

interface InventoryObject


interface Menu {
    fun open(player: Player): InventoryMenu
}


fun Menu.sendConfirmation(player: Player, title: String, confirmationInfo: List<Text>, onConfirmation: () -> Unit) {
    InventoryMenu(player, 3, plain(title)) {
}
}


