package com.mineinabyss.geary.papermc.spawning.conditions

import com.mineinabyss.geary.actions.ActionGroupContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.mineinabyss.geary.actions.Condition
import com.mineinabyss.geary.papermc.spawning.config.SpawnLocationsConfig
import com.mineinabyss.geary.papermc.location

@Serializable
@SerialName("geary:in_regions")
class InRegionsCondition(
    val regions: List<String>,
): Condition
{
     override fun ActionGroupContext.execute(): Boolean {
         // todo: retrieve config
         val config : SpawnLocationsConfig = TODO()
         val loc = location ?: return false;


//         return regions.any { testedRegion ->
//             config.Locations[testedRegion]?.isInside(loc) == true
//         }
         for (tested_region in regions) {
             for (region in config.Locations.keys) {
                 if (tested_region == region) {
                     val regionDef = config.Locations[region]?: continue
                     if (regionDef.isInside(loc)) {
                         return true;
                         }

                 }
             }
         }


        return false
    }
}