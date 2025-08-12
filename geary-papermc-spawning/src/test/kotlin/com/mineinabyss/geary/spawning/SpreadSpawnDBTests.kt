package com.mineinabyss.geary.spawning

import com.mineinabyss.geary.papermc.spawning.database.dao.SpawnLocationsDAO
import com.mineinabyss.geary.papermc.spawning.database.dao.StoredEntity
import com.mineinabyss.geary.papermc.spawning.database.schema.SpawningSchema
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.coroutines.test.runTest
import me.dvyy.sqlite.Database
import org.bukkit.Location
import org.bukkit.World
import org.junit.jupiter.api.Test
import java.util.*

class SpreadSpawnDBTests {
    @Test
    fun `db should get spawns near correctly`() = runTest {
        val world = mockkClass(World::class) {
            every { uid } returns UUID.fromString("887fe8dd-9a13-46b7-bb46-052150ef27d9")
        }
        val db = Database.temporary { SpawningSchema(listOf(world)).init() }

        val locs = SpawnLocationsDAO()
        val nearbyResult = db.write {
            locs.insertSpawnLocation(Location(world, 1000.0, 0.0, 0.0), StoredEntity("hello"))
            locs.getSpawnsNear(Location(world, 1000.0, 0.0, 0.0), 10.0)
        }
        val farAwayResult = db.read {
            locs.getSpawnsNear(Location(world, 0.0, 0.0, 0.0), 10.0)
        }

        nearbyResult.shouldHaveSize(1)
        nearbyResult.first().should {
            it.stored.type shouldBe "hello"
            it.location shouldBe Location(world, 1000.0, 0.0, 0.0)
        }
        farAwayResult.shouldBeEmpty()
    }
}
