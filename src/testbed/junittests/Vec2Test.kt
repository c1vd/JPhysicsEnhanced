package testbed.junittests

import junit.framework.TestCase
import library.math.Vec2
import org.junit.Assert
import org.junit.Test

class Vec2Test {
    @Test
    fun setUsingDoubleParameters() {
        val vec = Vec2(4.0, 2.0)
        vec.set(33.0, 7.0)
        TestCase.assertEquals(33.0, vec.x, 0.0)
        TestCase.assertEquals(7.0, vec.y, 0.0)
    }

    @Test
    fun setToVector() {
        val vec = Vec2(103.2, -2489273423.2)
        vec.set(Vec2(-42.4, 92.1))
        TestCase.assertEquals(vec.x, -42.4, 0.0)
        TestCase.assertEquals(vec.y, 92.1, 0.0)
    }

    @Test
    fun copy() {
        val vec1 = Vec2(1.0, 1.0)
        val vec2 = vec1.copy()
        Assert.assertNotEquals(vec1, vec1.copy())
        TestCase.assertEquals(vec1.x, vec2.x, 0.0)
        TestCase.assertEquals(vec1.y, vec2.y, 0.0)
    }

    @Test
    fun negative() {
        val vec = Vec2(5.0, -7.0)
        vec.negative()
        TestCase.assertEquals(vec.x, -5.0, 0.0)
        TestCase.assertEquals(vec.y, 7.0, 0.0)

        val vec1 = vec.negative()
        TestCase.assertEquals(vec1.x, 5.0, 0.0)
        TestCase.assertEquals(vec1.y, -7.0, 0.0)
        TestCase.assertEquals(vec.x, 5.0, 0.0)
        TestCase.assertEquals(vec.y, -7.0, 0.0)
    }

    @Test
    fun negativeVec() {
        val vec1 = Vec2(5.0, 1.0)
        val vec2 = -vec1
        TestCase.assertEquals(5.0, vec1.x, 0.0)
        TestCase.assertEquals(1.0, vec1.y, 0.0)
        TestCase.assertEquals(-5.0, vec2.x, 0.0)
        TestCase.assertEquals(-1.0, vec2.y, 0.0)
    }

    @Test
    fun add() {
        val vec1 = Vec2(5.0, 2.0)
        val vec2 = Vec2(7.0, 1.0)
        vec1.add(vec2)
        TestCase.assertEquals(12.0, vec1.x, 0.0)
        TestCase.assertEquals(3.0, vec1.y, 0.0)
    }

    @Test
    fun addi() {
        val vec1 = Vec2(5.0, 2.0)
        var vec2 = Vec2(7.0, 1.0)
        vec2 = vec1 + vec2
        TestCase.assertEquals(5.0, vec1.x, 0.0)
        TestCase.assertEquals(2.0, vec1.y, 0.0)
        TestCase.assertEquals(12.0, vec2.x, 0.0)
        TestCase.assertEquals(3.0, vec2.y, 0.0)
    }

    @Test
    fun normal() {
        val vec1 = Vec2(0.0, 1.0)
        val `val` = vec1.normal()
        TestCase.assertEquals(-1.0, `val`.x, 0.0)
        TestCase.assertEquals(0.0, `val`.y, 0.0)
    }

    @Test
    fun normalize() {
        val vec1 = Vec2(-345.34, 745.0)
        vec1.normalize()
        TestCase.assertEquals(vec1.length(), 1.0, 0.0)
        TestCase.assertEquals(vec1.x, -0.4205573495355269, 0.0)
        TestCase.assertEquals(vec1.y, 0.9072659564602061, 0.0)
    }

    @Test
    fun getNormalize() {
        val vec1 = Vec2(1.0, 7.0)
        val `val` = vec1.normalized
        TestCase.assertEquals(0.1414213562373095, `val`.x, 0.0)
        TestCase.assertEquals(0.9899494936611665, `val`.y, 0.0)

        TestCase.assertEquals(1.0, vec1.x, 0.0)
        TestCase.assertEquals(7.0, vec1.y, 0.0)
    }

    @Test
    fun distance() {
        val vec1 = Vec2(5.0, 2.0)
        val vec2 = Vec2(7.0, 1.0)
        val dist = vec1.distance(vec2)
        //square root of 5
        TestCase.assertEquals(2.2361, dist, 0.0001)
    }

    @Test
    fun subtract() {
        var vec1 = Vec2(5.0, 2.0)
        val vec2 = Vec2(7.0, 1.0)
        vec1 = vec1 - vec2
        TestCase.assertEquals(-2.0, vec1.x, 0.0)
        TestCase.assertEquals(1.0, vec1.y, 0.0)
    }

    @Test
    fun vectorCrossProduct() {
        val vec1 = Vec2(2.0, 3.0)
        val vec2 = Vec2(5.0, 6.0)
        val i = vec1.crossProduct(vec2)
        TestCase.assertEquals(-3.0, i, 0.0)
    }

    @Test
    fun scalarCrossProduct() {
        val vec1 = Vec2(2.0, 3.0)
        val cross = vec1.crossProduct(4.0)
        TestCase.assertEquals(2.0, vec1.x, 0.0)
        TestCase.assertEquals(3.0, vec1.y, 0.0)

        TestCase.assertEquals(-12.0, cross.x, 0.0)
        TestCase.assertEquals(8.0, cross.y, 0.0)
    }

    @Test
    fun scalar() {
        val vec1 = Vec2(5.0, 2.0)
        val vec2 = vec1.scalar(4.0)
        TestCase.assertEquals(5.0, vec1.x, 0.0)
        TestCase.assertEquals(2.0, vec1.y, 0.0)
        TestCase.assertEquals(20.0, vec2.x, 0.0)
        TestCase.assertEquals(8.0, vec2.y, 0.0)
    }

    @Test
    fun dotProduct() {
        val vec1 = Vec2(5.0, 1.0)
        val vec2 = Vec2(15.0, 10.0)
        val i = vec1.dotProduct(vec2)
        TestCase.assertEquals(85.0, i, 0.0)
    }

    @Test
    fun length() {
        val vec1 = Vec2(0.0, 7.0)
        val `val` = vec1.length()
        TestCase.assertEquals(7.0, `val`, 0.0)
    }

    @Test
    fun isValid() {
        Assert.assertTrue(Vec2(-34234.234234324, -324954.5).isValid)
        Assert.assertTrue(Vec2(32454543.0, 543543534.6).isValid)
        Assert.assertTrue(Vec2(Double.Companion.MAX_VALUE, -324954.5).isValid)
        Assert.assertTrue(Vec2(32454543.0, -Double.Companion.MAX_VALUE).isValid)
        Assert.assertFalse(Vec2(32454543.0, Double.Companion.POSITIVE_INFINITY).isValid)
        Assert.assertFalse(Vec2(32454543.0, -Double.Companion.POSITIVE_INFINITY).isValid)

        Assert.assertFalse(Vec2(Double.Companion.POSITIVE_INFINITY, 234.534).isValid)
        Assert.assertFalse(Vec2(-Double.Companion.POSITIVE_INFINITY, 234.534324).isValid)

        //Has to be double inputs not integer or doesn't detect NAN
        Assert.assertFalse(Vec2(0.0 / 0, 234.534).isValid)
        Assert.assertFalse(Vec2(34255234.4, 0.0 / 0).isValid)
    }

    @Test
    fun isZero() {
        val v = Vec2()

        Assert.assertTrue(v.isZero)

        v.set(1.0, 0.0)
        Assert.assertFalse(v.isZero)

        v.set(1.0, 1.0)
        Assert.assertFalse(v.isZero)

        v.set(0.0, 1.0)
        Assert.assertFalse(v.isZero)
    }

    @Test
    fun createArray() {
        val vectorList = arrayOfNulls<Vec2>(10)
        for (v in vectorList) {
            Assert.assertNull(v)
        }
        TestCase.assertEquals(vectorList.size, 10)
    }

    @Test
    fun directionConstructor() {
        val v = Vec2(2.0, 3.0)
        v.add(Vec2(-1.2, -5.4))
        TestCase.assertEquals(0.8, v.x, 0.0)
        TestCase.assertEquals(-2.4, v.y, 1e-15)
    }
}