package library.rays

import library.dynamics.Body
import library.geometry.Circle
import library.geometry.Polygon
import library.math.Vec2
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import kotlin.math.sqrt

/**
 * Ray class to define and project rays in a world.
 */
class Ray(private var startPoint: Vec2, direction: Vec2, private val distance: Int) {
    /**
     * Gets the direction of the ray in radians.
     *
     * @return direction variable of type Vectors2D.
     */

    var direction: Vec2 = direction.normalized

    /**
     * Sets the origin of the rays projection.
     *
     * @param v Vectords2D positoin in world space.
     */
    fun setStartPoint(v: Vec2) {
        this.startPoint = v
    }

    /**
     * Convenience constructor with ray set at origin. Similar to
     * [.Ray]
     *
     * @param direction The direction of the ray points in radians.
     * @param distance  The distance the ray is projected
     */
    constructor(direction: Double, distance: Int) : this(Vec2(), Vec2(direction), distance)

    /**
     * Convenience constructor with ray set at origin. Similar to
     * [.Ray]
     *
     * @param direction The direction of the ray points.
     * @param distance  The distance the ray is projected
     */
    constructor(direction: Vec2, distance: Int) : this(Vec2(), direction, distance)

    /**
     * Convenience constructor. Similar to
     * [.Ray]
     *
     * @param startPoint The origin of the rays projection.
     * @param direction  The direction of the ray points in radians.
     * @param distance   The distance the ray is projected
     */
    constructor(startPoint: Vec2, direction: Double, distance: Int) : this(startPoint, Vec2(direction), distance)

    var rayInformation: RayInformation? = null
        private set

    /**
     * Updates the projection in world space and acquires information about the closest intersecting object with the ray projection.
     *
     * @param bodiesToEvaluate Arraylist of bodies to check if they intersect with the ray projection.
     */
    fun updateProjection(bodiesToEvaluate: ArrayList<Body>) {
        this.rayInformation = null
        val endPoint = direction * distance.toDouble()
        val end_x = endPoint.x
        val end_y = endPoint.y

        var min_t1 = Double.Companion.POSITIVE_INFINITY
        var min_px = 0.0
        var min_py = 0.0
        var intersectionFound = false
        var closestBody: Body? = null

        for (body in bodiesToEvaluate) {
            if (body.shape is Polygon) {
                val poly = body.shape as Polygon
                for (i in poly.vertices.indices) {
                    var startOfPolyEdge = poly.vertices[i]
                    var endOfPolyEdge = poly.vertices[if (i + 1 == poly.vertices.size) 0 else i + 1]
                    startOfPolyEdge = poly.orient.mul(startOfPolyEdge, Vec2()) + body.position
                    endOfPolyEdge = poly.orient.mul(endOfPolyEdge, Vec2()) + body.position
                    val dx = endOfPolyEdge.x - startOfPolyEdge.x
                    val dy = endOfPolyEdge.y - startOfPolyEdge.y

                    //Check to see if the lines are not parallel
                    if ((dx - end_x) != 0.0 && (dy - end_y) != 0.0) {
                        val t2 =
                            (end_x * (startOfPolyEdge.y - startPoint.y) + (end_y * (startPoint.x - startOfPolyEdge.x))) / (dx * end_y - dy * end_x)
                        val t1 = (startOfPolyEdge.x + dx * t2 - startPoint.x) / end_x

                        if (t1 > 0 && t2 >= 0 && t2 <= 1.0) {
                            val point = Vec2(startPoint.x + end_x * t1, startPoint.y + end_y * t1)
                            val dist = (point - startPoint).length
                            if (t1 < min_t1 && dist < distance) {
                                min_t1 = t1
                                min_px = point.x
                                min_py = point.y
                                intersectionFound = true
                                closestBody = body
                            }
                        }
                    }
                }
            } else if (body.shape is Circle) {
                val circle = body.shape as Circle
                val ray = endPoint.copy()
                val circleCenter = body.position.copy()
                val r = circle.radius
                val difInCenters = startPoint - circleCenter

                val a = ray.dotProduct(ray)
                val b = 2 * difInCenters.dotProduct(ray)
                val c = difInCenters.dotProduct(difInCenters) - r * r

                var discriminant = b * b - 4 * a * c
                if (discriminant >= 0) {
                    discriminant = sqrt(discriminant)

                    val t1 = (-b - discriminant) / (2 * a)
                    if (t1 >= 0 && t1 <= 1) {
                        if (t1 < min_t1) {
                            min_t1 = t1
                            min_px = startPoint.x + end_x * t1
                            min_py = startPoint.y + end_y * t1
                            intersectionFound = true
                            closestBody = body
                        }
                    }
                }
            }
        }
        if (intersectionFound) {
            this.rayInformation = RayInformation(closestBody, min_px, min_py, -1)
        }
    }

    /**
     * Debug draw method for ray projection.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    fun draw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera) {
        g.color = paintSettings.projectedRay
        val epicenter = camera.convertToScreen(startPoint)
        val endPoint = camera.convertToScreen(direction * distance.toDouble() + startPoint)
        g.draw(Line2D.Double(epicenter.x, epicenter.y, endPoint.x, endPoint.y))

        g.color = paintSettings.rayToBody
        if (this.rayInformation != null) {
            val intersection = camera.convertToScreen(rayInformation!!.coord)
            g.draw(Line2D.Double(epicenter.x, epicenter.y, intersection.x, intersection.y))

            val circleRadius = camera.scaleToScreenXValue(paintSettings.RAY_DOT)
            g.fill(
                Ellipse2D.Double(
                    intersection.x - circleRadius,
                    intersection.y - circleRadius,
                    2.0 * circleRadius,
                    2.0 * circleRadius
                )
            )
        }
    }
}

