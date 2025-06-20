package testbed.junittests

import junit.framework.TestCase
import library.collision.AABB
import library.collision.AABB.Companion.AABBOverLap
import library.dynamics.Body
import library.geometry.Circle
import library.math.Vec2
import org.junit.Assert
import org.junit.Test

class AABBTest {
    @Test
    fun set() {
        val b = AABB()
        val a = AABB(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        b.set(a)
        val `val` = Vec2(10.0, 10.0)
        TestCase.assertEquals(`val`.x, b.min.x, 0.0)
        TestCase.assertEquals(`val`.y, b.min.y, 0.0)
        TestCase.assertEquals(Vec2(20.0, 20.0).x, b.max.x, 0.0)
        TestCase.assertEquals(Vec2(20.0, 20.0).y, b.max.y, 0.0)
    }

    @Test
    fun getMin() {
        val b = AABB()
        val a = AABB(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        b.set(a)
        val `val` = Vec2(10.0, 10.0)
        TestCase.assertEquals(`val`.x, b.min.x, 0.0)
        TestCase.assertEquals(`val`.y, b.min.y, 0.0)
    }

    @Test
    fun getMax() {
        val b = AABB()
        val a = AABB(Vec2(10.0, 10.0), Vec2(20.0, 20.0))
        b.set(a)
        TestCase.assertEquals(Vec2(20.0, 20.0).x, b.max.x, 0.0)
        TestCase.assertEquals(Vec2(20.0, 20.0).y, b.max.y, 0.0)
    }

    @Test
    fun isValid() {
        var a = AABB(Vec2(100.0, 100.0), Vec2(300.0, 300.0))
        TestCase.assertTrue(a.isValid)
        a = AABB(Vec2(0.0, 0.0), Vec2(0.0, 0.0))
        TestCase.assertTrue(a.isValid)
        a = AABB(Vec2(Double.Companion.POSITIVE_INFINITY, 0.0), Vec2(300.0, 300.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, 0.0), Vec2(Double.Companion.POSITIVE_INFINITY, 300.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, 0.0), Vec2(1.0, Double.Companion.POSITIVE_INFINITY))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, Double.Companion.POSITIVE_INFINITY), Vec2(1.0, 1.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, -Double.Companion.POSITIVE_INFINITY), Vec2(1.0, 1.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, 0.0 / 0), Vec2(1.0, 1.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(0.0, 0.0 / 0), Vec2(0.0 / 0, 1.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(-0.0, -0.0), Vec2(-0.0, -0.0))
        TestCase.assertTrue(a.isValid)
        a = AABB(Vec2(-10.0, -10.0), Vec2(-0.0, -0.0))
        TestCase.assertTrue(a.isValid)
        a = AABB(Vec2(-0.0, -0.0), Vec2(-10.0, -10.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(-10.0, -0.0), Vec2(-10.0, -10.0))
        TestCase.assertFalse(a.isValid)
        a = AABB(Vec2(-10.0, -0.0), Vec2(-10.0, -10.0))
        TestCase.assertFalse(a.isValid)
    }

    @Test
    fun AABBOverLap() {
        //Corner overlaps - top right
        var a = AABB(Vec2(100.0, 100.0), Vec2(300.0, 300.0))
        var b = AABB(Vec2(200.0, 200.0), Vec2(400.0, 400.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Corner overlaps - top left
        a = AABB(Vec2(0.0, 0.0), Vec2(200.0, 200.0))
        b = AABB(Vec2(-100.0, 100.0), Vec2(100.0, 300.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Corner overlaps - bottom left
        a = AABB(Vec2(0.0, 0.0), Vec2(200.0, 200.0))
        b = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        TestCase.assertTrue(AABBOverLap(a, b))


        //Corner overlaps - bottom right
        a = AABB(Vec2(0.0, 0.0), Vec2(200.0, 200.0))
        b = AABB(Vec2(100.0, -100.0), Vec2(300.0, 100.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - middle left
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-150.0, -50.0), Vec2(50.0, 50.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - middle right
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(50.0, -50.0), Vec2(150.0, 50.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - middle
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-150.0, -50.0), Vec2(150.0, 50.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - top
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, -50.0), Vec2(50.0, 150.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - bottom
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, -150.0), Vec2(50.0, 50.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Middle overlaps - bottom
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, -150.0), Vec2(50.0, 150.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //With in
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, -50.0), Vec2(50.0, 50.0))
        TestCase.assertTrue(AABBOverLap(a, b))

        //Quadrant 1
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-200.0, 200.0), Vec2(100.0, 500.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 2
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, 200.0), Vec2(50.0, 500.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 3
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(500.0, 200.0), Vec2(570.0, 500.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 4
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-200.0, -50.0), Vec2(-150.0, 50.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 6
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(200.0, -50.0), Vec2(250.0, 50.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 7
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-200.0, -2000.0), Vec2(-100.0, -500.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 8
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(-80.0, -800.0), Vec2(50.0, -500.0))
        TestCase.assertFalse(AABBOverLap(a, b))

        //Quadrant 9
        a = AABB(Vec2(-100.0, -100.0), Vec2(100.0, 100.0))
        b = AABB(Vec2(500.0, -700.0), Vec2(570.0, -500.0))
        TestCase.assertFalse(AABBOverLap(a, b))
    }

    @Test
    fun testAABBOverLap() {
        val b = AABB(Vec2(100.0, 300.0), Vec2(300.0, 100.0))
        var point = Vec2(100.0, 100.0)
        TestCase.assertTrue(b.AABBOverLap(point))
        //Checks if its inside
        point = Vec2(150.0, 120.0)
        TestCase.assertTrue(b.AABBOverLap(point))

        point = Vec2(100.0, 100.0)
        TestCase.assertTrue(b.AABBOverLap(point))
        point = Vec2(100.0, 300.0)
        TestCase.assertTrue(b.AABBOverLap(point))

        //Checks if its outside
        point = Vec2(50.0, 100.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(50.0, 50.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(150.0, 50.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(350.0, 50.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(350.0, 200.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(350.0, 500.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(200.0, 500.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(50.0, 500.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(50.0, 200.0)
        TestCase.assertFalse(b.AABBOverLap(point))

        point = Vec2(100.0, 500.0)
        TestCase.assertFalse(b.AABBOverLap(point))
        point = Vec2(500.0, 100.0)
        TestCase.assertFalse(b.AABBOverLap(point))
    }

    @Test
    fun copy() {
        val a = AABB(Vec2(-10.0, 10.0), Vec2(10.0, 10.0))
        val b = a.copy()

        Assert.assertNotSame(b, a)
        TestCase.assertEquals(a.min.x, b.min.x)
        TestCase.assertEquals(a.min.y, b.min.y)
        TestCase.assertEquals(a.max.x, b.max.x)
        TestCase.assertEquals(a.max.y, b.max.y)
    }

    @Test
    fun addOffset() {
        val a = AABB(Vec2(-10.0, 10.0), Vec2(10.0, 10.0))
        a.addOffset(Vec2(10.0, 10.0))
        TestCase.assertEquals(a.min.x, 0.0)
        TestCase.assertEquals(a.min.y, 20.0)
        TestCase.assertEquals(a.max.x, 20.0)
        TestCase.assertEquals(a.max.y, 20.0)
    }

    @Test
    fun BodyOverlap() {
        val a = Body(Circle(20.0), 0.0, 0.0)
        val b = Body(Circle(20.0), 0.0, 0.0)
        TestCase.assertTrue(AABBOverLap(a, b))
        a.position!!.add(Vec2(41.0, 0.0))
        TestCase.assertFalse(AABBOverLap(a, b))
        a.position!!.add(Vec2(-6.0, 10.0))
        TestCase.assertTrue(AABBOverLap(a, b))
        a.position!!.add(Vec2(-34.0, -38.0))
        TestCase.assertTrue(AABBOverLap(a, b))
    }
}