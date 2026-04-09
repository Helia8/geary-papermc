package com.mineinabyss.geary.papermc.spawning.conditions

import com.mineinabyss.dependencies.get
import com.mineinabyss.geary.actions.ActionGroupContext
import com.mineinabyss.geary.actions.Condition
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.papermc.location
import com.mineinabyss.geary.papermc.spawning.SpawningFeature
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsConfig
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsUnified
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("geary:include_spawn_tag")
class IncludeSpawnTagCondition(
    val tags : List<String>,
): Condition {
    override fun ActionGroupContext.execute(): Boolean {
        val unified = gearyPaper.features.get(SpawningFeature)?.get<SpawnLocationsUnified>() ?: return false

        val config = unified.unified;
        val loc = location ?: return false;

        for (tag in tags) {
            for (regionDef in config.values) {
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