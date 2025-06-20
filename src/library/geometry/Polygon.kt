package library.geometry

import library.collision.AABB
import library.math.Vec2
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * Class for representing polygon shape.
 */
class Polygon : Shape {
    var vertices: Array<Vec2>
    lateinit var normals: Array<Vec2>

    /**
     * Constructor takes a supplied list of vertices and generates a convex hull around them.
     *
     * @param vertList Vertices of polygon to create.
     */
    constructor(vertList: Array<Vec2?>) {
        this.vertices = generateHull(vertList, vertList.size)
        calcNormals()
    }

    /**
     * Constructor to generate a rectangle.
     *
     * @param width  Desired width of rectangle
     * @param height Desired height of rectangle
     */
    constructor(width: Double, height: Double) {
        vertices = arrayOf(
            Vec2(-width, -height),
            Vec2(width, -height),
            Vec2(width, height),
            Vec2(-width, height)
        )
        normals = arrayOf(
            Vec2(0.0, -1.0),
            Vec2(1.0, 0.0),
            Vec2(0.0, 1.0),
            Vec2(-1.0, 0.0)
        )

    }

    /**
     * Generate a regular polygon with a specified number of sides and size.
     *
     * @param radius    The maximum distance any vertex is away from the center of mass.
     * @param noOfSides The desired number of face the polygon has.
     */
    constructor(radius: Int, noOfSides: Int) {
        vertices = (0..<noOfSides).map {
            val angle = 2 * Math.PI / noOfSides * (it + 0.75)
            val pointX = radius * StrictMath.cos(angle)
            val pointY = radius * StrictMath.sin(angle)
            Vec2(pointX, pointY)
        }.toTypedArray()

        calcNormals()
    }

    /**
     * Generates normals for each face of the polygon. Positive normals of polygon faces face outward.
     */
    fun calcNormals() {
        normals = vertices.indices.map {
            -((vertices[if (it + 1 == vertices.size) 0 else it + 1] - vertices[it]).normal().normalized)
        }.toTypedArray()
    }

    /**
     * Implementation of calculating the mass of a polygon.
     *
     * @param density The desired density to factor into the calculation.
     */
    override fun calcMass(density: Double) {
        var centroidDistVec = Vec2(0.0, 0.0)
        var area = 0.0
        var I = 0.0
        val k = 1.0 / 3.0

        for (i in vertices.indices) {
            val point1 = vertices[i]
            val point2 = vertices[(i + 1) % vertices.size]
            val areaOfParallelogram = point1.crossProduct(point2)
            val triangleArea = 0.5 * areaOfParallelogram
            area += triangleArea

            val weight = triangleArea * k
            centroidDistVec.add(point1.scalar(weight))
            centroidDistVec.add(point2.scalar(weight))

            val intx2 = point1.x * point1.x + point2.x * point1.x + point2.x * point2.x
            val inty2 = point1.y * point1.y + point2.y * point1.y + point2.y * point2.y
            I += (0.25 * k * areaOfParallelogram) * (intx2 + inty2)
        }
        centroidDistVec = centroidDistVec.scalar(1.0 / area)

        for (i in vertices.indices) {
            vertices[i] = vertices[i] - centroidDistVec
        }

        body.mass = density * area
        body.invMass = if (body.mass != 0.0) 1.0 / body.mass else 0.0
        body.I = I * density
        body.invI = if (body.I != 0.0) 1.0 / body.I else 0.0
    }

    /**
     * Generates an AABB encompassing the polygon and binds it to the body.
     */
    override fun createAABB() {
        val firstPoint = orient.mul(vertices[0], Vec2())
        var minX = firstPoint.x
        var maxX = firstPoint.x
        var minY = firstPoint.y
        var maxY = firstPoint.y

        for (i in 1..<vertices.size) {
            val point = orient.mul(vertices[i], Vec2())
            val px = point.x
            val py = point.y

            if (px < minX) {
                minX = px
            } else if (px > maxX) {
                maxX = px
            }

            if (py < minY) {
                minY = py
            } else if (py > maxY) {
                maxY = py
            }
        }
        body.aabb = AABB(Vec2(minX, minY), Vec2(maxX, maxY))
    }


    /**
     * Debug draw method for a polygon.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    override fun draw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera) {
        val s = Path2D.Double()
        for (i in vertices.indices) {
            var v = Vec2(this.vertices[i])
            orient.mul(v)
            v.add(body.position)
            v = camera.convertToScreen(v)
            if (i == 0) {
                s.moveTo(v.x, v.y)
            } else {
                s.lineTo(v.x, v.y)
            }
        }
        s.closePath()
        if (body.mass == 0.0) {
            g.color = paintSettings.staticFill
            g.fill(s)
            g.color = paintSettings.staticOutLine
        } else {
            g.color = paintSettings.shapeFill
            g.fill(s)
            g.color = paintSettings.shapeOutLine
        }
        g.draw(s)
    }

    /**
     * Generates a convex hull around the vertices supplied.
     *
     * @param vertices List of vertices.
     * @param n        Number of vertices supplied.
     * @return Convex hull array.
     */
    private fun generateHull(vertices: Array<Vec2?>, n: Int): Array<Vec2> {
        val hull = ArrayList<Vec2>()

        var firstPointIndex = 0
        var minX = Double.Companion.MAX_VALUE
        for (i in 0..<n) {
            val x = vertices[i]!!.x
            if (x < minX) {
                firstPointIndex = i
                minX = x
            }
        }

        var point = firstPointIndex
        var currentEvalPoint: Int
        var first = true
        while (point != firstPointIndex || first) {
            first = false
            hull.add(vertices[point]!!)
            currentEvalPoint = (point + 1) % n
            for (i in 0..<n) {
                if (sideOfLine(vertices[point]!!, vertices[i]!!, vertices[currentEvalPoint]!!) == -1) currentEvalPoint =
                    i
            }
            point = currentEvalPoint
        }


        return hull.toTypedArray()
    }

    /**
     * Checks which side of a line a point is on.
     *
     * @param p1    Vertex of line to evaluate.
     * @param p2    Vertex of line to evaluate.
     * @param point Point to check which side it lies on.
     * @return Int value - positive = right side of line. Negative = left side of line.
     */
    private fun sideOfLine(p1: Vec2, p2: Vec2, point: Vec2): Int {
        val `val` = (p2.y - p1.y) * (point.x - p2.x) - (p2.x - p1.x) * (point.y - p2.y)
        return when {
            `val` > 0.0 -> 1
            `val` == 0.0 -> 0
            else -> -1
        }
    }
}