package com.mineinabyss.geary.papermc.spawning.conditions

import com.mineinabyss.geary.actions.ActionGroupContext
import com.mineinabyss.geary.actions.Condition
import com.mineinabyss.geary.papermc.location
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:include_spawn_tag")
class IncludeSpawnTagCondition(
    val tags : List<String>,
): Condition {
    override fun ActionGroupContext.execute(): Boolean {
        // todo: retrieve config
        val config: SpawnLocationsConfig = TODO()
        val loc = location ?: return false;

        for (tag in tags) {
            for (regionDef in config.Locations.values) {
                if (regionDef.group == tag) {
                    if (regionDef.isInside(loc)) {
                        return true;
                    }
                }
            }
        }


        return false
    }
}