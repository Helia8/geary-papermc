package com.mineinabyss.geary.papermc.spawning.conditions

import com.mineinabyss.geary.actions.ActionGroupContext
import com.mineinabyss.geary.actions.Condition
import com.mineinabyss.geary.papermc.location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("geary:spawn_region")
class LocalizedSpawningCondition(
    val region_name: String,
    val first_corner: Triple<Int, Int, Int>,
    val second_corner: Triple<Int, Int, Int>,
): Condition {

    private val min_xyz = Triple(
        minOf(first_corner.first, second_corner.first),
        minOf(first_corner.second, second_corner.second),
        minOf(first_corner.third, second_corner.third)
    )
    private val max_xyz = Triple(
        maxOf(first_corner.first, second_corner.first),
        maxOf(first_corner.second, second_corner.second),
        maxOf(first_corner.third, second_corner.third)
    )
    override fun ActionGroupContext.execute(): Boolean {
        val location = location ?: return false
        val x = location.blockX
        val y = location.blockY
        val z = location.blockZ
        if (x < min_xyz.first || x > max_xyz.first) return false
        if (y < min_xyz.second || y > max_xyz.second) return false
        if (z < min_xyz.third || z > max_xyz.third) return false
        return true
    }


}