package testbed.demo.tests

import library.dynamics.World
import library.explosions.RaycastExplosion
import library.math.Vec2
import testbed.demo.TestBedWindow

object RaycastExplosionTest {
    @JvmField
    val text: Array<String?> = arrayOf<String?>("Raycast Explosions:", "Left click: casts an explosion")
    @JvmField
    var active: Boolean = false
    @JvmField
    var r: RaycastExplosion? = null

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        testBedWindow.setCamera(Vec2(0.0, 300.0), 2.0)
        val temp = testBedWindow.world
        active = true

        testBedWindow.buildExplosionDemo()

        r = RaycastExplosion(Vec2(0.0, 1.0), 100, 1000, temp.bodies)
        testBedWindow.add(r)
    }
}
