package testbed.demo.tests

import library.dynamics.World
import library.math.Vec2
import testbed.demo.TestBedWindow

object CompoundBodies {
    val text: Array<String?> = arrayOf<String?>("Compound Bodies:")

    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        val temp = testBedWindow.world
    }
}