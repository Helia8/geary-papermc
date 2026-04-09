package com.mineinabyss.geary.papermc.spawning.conditions

import com.mineinabyss.dependencies.DI
import com.mineinabyss.dependencies.get
import com.mineinabyss.geary.actions.ActionGroupContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.mineinabyss.geary.actions.Condition
import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsConfig
import com.mineinabyss.geary.papermc.location
import com.mineinabyss.geary.papermc.spawning.SpawningFeature
import com.mineinabyss.geary.papermc.spawning.config.SpawnConfig
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsUnified

@Serializable
@SerialName("geary:in_regions")
class InRegionsCondition(
    val regions: List<String>,
): Condition
{
     override fun ActionGroupContext.execute(): Boolean {

         val unified = gearyPaper.features.get(SpawningFeature)?.get<SpawnLocationsUnified>() ?: return false

         val config = unified.unified;
         val loc = location ?: return false;


//         return regions.any { testedRegion ->
//             config.Locations[testedRegion]?.isInside(loc) == true
//         }
         for (testedRegion in regions) {
             for (region in config.keys) {
                 if (testedRegion == region) {
                     val regionDef = config[region]?: continue
                     if (regionDef.isInside(loc)) {
                         return true;
                         }

                 }
             }
         }


        return false
    }
}