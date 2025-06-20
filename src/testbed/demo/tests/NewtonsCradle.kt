package testbed.demo.tests

import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.joints.Joint
import library.joints.JointToPoint
import library.math.Vec2
import testbed.demo.TestBedWindow

object NewtonsCradle {
    @JvmField
    val text: Array<String?> = arrayOf<String?>("Newtons Cradle:")

    @JvmStatic
    fun load(testBedWindow: TestBedWindow) {
        testBedWindow.world = World(Vec2(0.0, -9.81))
        val temp = testBedWindow.world
        testBedWindow.setCamera(Vec2(), 2.0)

        val radius = 40.0
        val noOfCircles = 8
        val spread = ((noOfCircles - 1) * 80 / 2.0)

        var minX: Double
        var maxX: Double
        minX = -spread + 40

        run {
            for (i in 0..<noOfCircles) {
                val x = minX + (i * 80)
                val b = Body(Circle(radius), x, -100.0)
                b.restitution = 1.0
                b.staticFriction = 0.0
                b.dynamicFriction = 0.0
                temp.addBody(b)

                val j: Joint = JointToPoint(Vec2(x, 200.0), b, 300.0, 200000.0, 1000.0, true, Vec2())
                temp.addJoint(j)
            }
        }

        run {
            minX -= 80.0
            val b = Body(Circle(radius), minX - 300, 200.0)
            b.restitution = 1.0
            b.staticFriction = 0.0
            b.dynamicFriction = 0.0
            temp.addBody(b)

            val j: Joint = JointToPoint(Vec2(minX, 200.0), b, 300.0, 200000.0, 1000.0, true, Vec2())
            temp.addJoint(j)
        }
    }
}
