package testbed.demo.tests

import library.collision.Arbiter.Companion.isPointInside
import library.dynamics.World
import library.math.Matrix2D
import library.math.Vec2
import library.rays.Ray
import testbed.demo.TestBedWindow

object Raycast {
    @JvmField
    val text: Array<String?> = arrayOf<String?>("Raycast:")
    @JvmField
    var active: Boolean = false

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        testBedWindow.setCamera(Vec2(-100.0, -20.0), 3.3)
        active = true

        var isValid = false
        while (!isValid) {
            isValid = true
            testBedWindow.generateBoxOfObjects()
            for (b in testBedWindow.world.bodies) {
                if (isPointInside(b, Vec2())) {
                    isValid = false
                    testBedWindow.world.clearWorld()
                    break
                }
            }
        }

        val r = Ray(Vec2(), Vec2(0.0, 1.0), 1000)
        testBedWindow.add(r)
    }

    @JvmStatic
    fun action(r: Ray) {
        val u = Matrix2D()
        u.set(-0.0006)
        u.mul(r.direction)
    }
}
