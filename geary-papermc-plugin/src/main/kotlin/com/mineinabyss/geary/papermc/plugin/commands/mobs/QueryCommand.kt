package com.mineinabyss.geary.papermc.plugin.commands.mobs

import com.mineinabyss.geary.papermc.gearyPaper
import com.mineinabyss.geary.papermc.tracking.entities.gearyMobs
import com.mineinabyss.geary.papermc.tracking.entities.helpers.getKeyStrings
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.IdoCommand
import com.mineinabyss.idofront.commands.brigadier.IdoPlayerCommandContext
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import org.bukkit.entity.Entity

internal fun IdoCommand.mobsQuery() {
    val mobs: List<String> by lazy {
        buildList {
            addAll(listOf("custom"))
            addAll(gearyMobs.query.prefabs.getKeyStrings())
        }
    }

    fun querySuggestions(query: String): List<String> {
        val parts = query.split("+")
        val withoutLast = query.substringBeforeLast("+", missingDelimiterValue = "").let {
            if (parts.size > 1) "$it+" else it
        }
        return mobs.asSequence().filter {
            it !in parts && (it.startsWith(parts.last()) ||
                    it.substringAfter(":").startsWith(parts.last()))
        }.take(20).map { "$withoutLast$it" }.toList()
    }

    ("remove" / "info") {
        val isInfo = name == "info"
        requiresPermission("geary.mobs.$name")
        val query by Args.word().suggests { suggest(querySuggestions(suggestions.remaining)) }
        playerExecutes { removeOrInfo(query(), 0, isInfo) }

        val radius by Args.integer(min = 0)
        playerExecutes { removeOrInfo(query(), radius(), isInfo) }
    }
}


private fun IdoPlayerCommandContext.removeOrInfo(query: String, radius: Int, isInfo: Boolean) {
    val worlds = gearyPaper.plugin.server.worlds
    var entityCount = 0
    val entities = mutableSetOf<Entity>()
    val types = query.split("+")

    for (world in worlds) for (entity in world.entities) {
        val geary = entity.toGearyOrNull() ?: continue
        // Only select entities that are instanced from a gearyMobs registered prefab
        if (!gearyMobs.query.isMob(geary)) continue

        if (types.any { type ->
                when (type) {
                    "custom" -> true
                    else -> {
                        val prefab = runCatching { PrefabKey.of(type).toEntityOrNull() }.getOrNull()
                            ?: commandException("No such prefab or selector $type")
                        geary.deepInstanceOf(prefab)
                    }
                }
            }) {
            val playerLoc = player.location
            if (radius <= 0 || entity.world == playerLoc.world && entity.location.distance(playerLoc) < radius) {
                entityCount++
                if (isInfo) entities += entity
                else entity.remove()
            }
        }
    }

    sender.success(
        """
                ${if (isInfo) "There are" else "Removed"}
                <b>$entityCount</b> entities matching your query
                ${if (radius <= 0) "in loaded chunks." else "in a radius of $radius blocks."}
                """.trimIndent().replace("\n", " ")
    )
    if (isInfo) {
        val mobs = entities
            .asSequence()
            .flatMap { it.toGeary().prefabs }
            .groupingBy { it }
            .eachCount()
            .filter { gearyMobs.query.isMobPrefab(it.key) }
            .entries
            .sortedByDescending { it.value }
            .toList()

        if (mobs.isNotEmpty()) sender.info(
            mobs.joinToString(separator = "\n") { (type, amount) ->
                val prefabName = type.get<PrefabKey>()?.toString() ?: type.toString()
                "<gray>${prefabName}</gray>: $amount"
            }
        )
    }
}
