package testbed.demo.tests

import library.dynamics.World
import library.math.Vec2
import testbed.demo.TestBedWindow

object ParticleExplosionTest {
    @JvmField
    val text: Array<String?> = arrayOf<String?>("Particle Explosions:", "Left click: casts an explosion")
    @JvmField
    var active: Boolean = false

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        testBedWindow.setCamera(Vec2(0.0, 300.0), 2.0)
        active = true

        testBedWindow.buildExplosionDemo()
    }
}