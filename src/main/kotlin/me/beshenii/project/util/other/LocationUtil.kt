package me.beshenii.project.util.other

import org.bukkit.Bukkit
import org.bukkit.Location

fun Location.posOffset(x : Double, y : Double, z : Double) : Location {
    return Location(this.world, this.x+x, this.y+y, this.z+z)
}

fun stringToLocation(location: String) : Location {
    val split = location.split(", ")
    return Location(Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
}

fun toString(location: Location) : String {
    return "${location.world.name}, ${location.x}, ${location.y}, ${location.z}"
}

fun listOfOffsets(location: Location, x: Double, y: Double, z: Double, times: Int) : MutableList<Location> {
    val list = mutableListOf(location)
    for (i in 1..times) {
        list += location.posOffset(x*i, y*i, z*i)
    }
    return list
}
