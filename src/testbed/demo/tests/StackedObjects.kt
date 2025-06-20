package testbed.demo.tests

import library.dynamics.Body
import library.dynamics.World
import library.geometry.Polygon
import library.math.Vec2
import testbed.demo.TestBedWindow

object StackedObjects {
    @JvmField
    val text: Array<String?> = arrayOf<String?>("Stacked Objects:")

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(0.0, 150.0), 1.8)

        run {
            for (x in 0..14) {
                for (y in 0..19) {
                    val b = Body(Polygon(10.0, 10.0), (-140 + (x * 20)).toDouble(), (-100 + (y * 20)).toDouble())
                    temp.addBody(b)
                }
            }
            for (x in 0..14) {
                val b = Body(Polygon(10.0, 10.0), (-140 + (x * 20)).toDouble(), 400.0)
                b.setDensity(10.0)
                temp.addBody(b)
            }

            val b = Body(Polygon(150.0, 10.0), 0.0, -120.0)
            b.setDensity(0.0)
            temp.addBody(b)
        }
    }
}
