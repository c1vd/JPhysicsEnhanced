package testbed.demo.tests

import library.dynamics.World
import library.math.Vec2
import testbed.demo.TestBedWindow

object Car {
    val text: Array<String?> = arrayOf<String?>("Car:")

    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, 0.0))
        val world = testBedWindow.world
        testBedWindow.setCamera(Vec2(0.0, 0.0), 1.4)
    }
}
