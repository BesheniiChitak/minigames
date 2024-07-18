package me.beshenii.project

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.beshenii.project.util.PlayerSave
import java.io.File

object StatsLoad {

    private val statsfolder = File(plugin.dataFolder, "variables")
    private val pluginfolder = plugin.dataFolder

    private val saveFile = File(statsfolder, "player_data.json")
    var stats = mutableMapOf<String, PlayerSave>()

    fun load() {
        if (!pluginfolder.exists()) pluginfolder.mkdirs()
        if (!statsfolder.exists()) statsfolder.mkdirs()
        else {
            if (!saveFile.exists()) saveFile.createNewFile()
            stats = Json.decodeFromString<MutableMap<String, PlayerSave>>(saveFile.readText())
        }
    }

    fun save() {
        saveFile.writeText(Json.encodeToString(stats))
    }
}
