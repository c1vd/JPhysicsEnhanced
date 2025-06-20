package testbed.demo.tests

import library.dynamics.Body
import library.dynamics.World
import library.geometry.Polygon
import library.math.Vec2
import testbed.demo.TestBedWindow

object SliceObjects {
    @JvmField
    val text: Array<String?> =
        arrayOf<String?>("Slice Objects:", "Left click: Click two points on the demo window to slice objects")
    @JvmField
    var active: Boolean = false

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        active = true
        testBedWindow.world = World(Vec2(0.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(0.0, 100.0), 1.3)

        val ground = Body(Polygon(10000.0, 2000.0), 0.0, -2040.0)
        ground.setDensity(0.0)
        temp.addBody(ground)

        testBedWindow.createTower(5, 0, -40)

        testBedWindow.scaleWorldFriction(0.4)
    }
}