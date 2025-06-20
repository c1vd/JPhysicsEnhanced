package testbed.junittests

import junit.framework.TestCase
import library.math.Matrix2D
import library.math.Vec2
import org.junit.Test

class Matrix2DTest {
    @Test
    fun setUsingRadians() {
        val m = Matrix2D()
        m.set(1.0)
        TestCase.assertEquals(m.row1.x, 0.5403023058681398)
        TestCase.assertEquals(m.row2.x, 0.8414709848078965)
        TestCase.assertEquals(m.row1.y, -0.8414709848078965)
        TestCase.assertEquals(m.row2.y, 0.5403023058681398)
    }

    @Test
    fun setUsingMatrix() {
        val m = Matrix2D()
        m.set(1.0)
        val u = Matrix2D()
        u.set(m)
        TestCase.assertEquals(u.row1.x, m.row1.x)
        TestCase.assertEquals(u.row2.x, m.row2.x)
        TestCase.assertEquals(u.row1.y, m.row1.y)
        TestCase.assertEquals(u.row2.y, m.row2.y)
    }

    @Test
    fun transpose() {
        val m = Matrix2D()
        m.set(1.0)
        val u = Matrix2D()
        u.set(m)
        TestCase.assertEquals(u.row1.x, m.row1.x)
        TestCase.assertEquals(u.row2.x, m.row2.x)
        TestCase.assertEquals(u.row1.y, m.row1.y)
        TestCase.assertEquals(u.row2.y, m.row2.y)
    }

    @Test
    fun mul() {
        val m = Matrix2D()
        m.set(1.0)
        val v = Vec2(1.0, 0.0)
        m.mul(v)
        TestCase.assertEquals(v.x, 0.5403023058681398)
        TestCase.assertEquals(v.y, 0.8414709848078965)
    }

    @Test
    fun testMul() {
        val m = Matrix2D()
        m.set(1.0)
        val v = Vec2(1.0, 0.0)
        val q = Vec2(10.0, 0.0)
        m.mul(v, q)
        TestCase.assertEquals(q.x, 0.5403023058681398)
        TestCase.assertEquals(q.y, 0.8414709848078965)
        TestCase.assertEquals(v.x, 1.0)
        TestCase.assertEquals(v.y, 0.0)
    }
}