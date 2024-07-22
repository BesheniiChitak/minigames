package me.beshenii.project.command

import me.beshenii.project.util.defaultSettings
import me.beshenii.project.util.other.color
import me.beshenii.project.util.other.green
import me.beshenii.project.util.other.red
import me.beshenii.project.util.settings
import net.kyori.adventure.text.Component.text
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object SettingCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (args.size != 2) return false
        val setting = args[0]
        val value = args[1]

        if (setting !in settings) {
            sender.sendMessage(text("Такой настройки не существует.").color(red))
            return false
        }
        if (settings[setting] == value) {
            sender.sendMessage(text("Настройка $setting уже установлена на $value.").color(red))
            return false
        }
        settings[setting] = value
        sender.sendMessage(text("Настройка $setting успешно установлена на $value.").color(green))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val list = mutableListOf<String>()
        val lastArg = args.last()
        val size = args.size
        when (size) {
            1 -> {
                list.addAll(settings.keys.filter { it.contains(lastArg) })
            }

            2 -> {
                val show = defaultSettings[args[0]] ?: "Неизвестная настройка!"
                val bools = listOf("true", "false")
                if (show in bools) list.addAll(bools)
                else list.add(show)
                val other = settings[args[0]] ?: show
                if (show != other) {
                    list.add(other)
                }
            }
        }
        return list.toMutableList()
    }
}


