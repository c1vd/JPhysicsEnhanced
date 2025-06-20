package library.explosions

import library.dynamics.Body
import library.math.Matrix2D
import library.math.Vec2
import library.rays.Ray
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D

/**
 * Models rayscatter explosions.
 */
class RayScatter(private var epicentre: Vec2, private val noOfRays: Int) {
    /**
     * Getter for rays.
     *
     * @return Array of all rays part of the ray scatter.
     */
    lateinit var rays: Array<Ray>

    /**
     * Getter for epicentre variable.
     *
     * @return Returns epicentre of explosion.
     */
    fun getEpicentre(): Vec2 {
        return epicentre
    }

    /**
     * Sets the epicentre to a different coordinate.
     *
     * @param v The vector position of the new epicentre.
     */
    fun setEpicentre(v: Vec2) {
        this.epicentre = v
        for (ray in rays) {
            ray.setStartPoint(epicentre)
        }
    }

    /**
     * Casts rays in 360 degrees with equal spacing.
     *
     * @param distance Distance of projected rays.
     */
    fun castRays(distance: Int) {
        val angle = 6.28319 / noOfRays
        val direction = Vec2(1.0, 1.0)
        val u = Matrix2D(angle)
        rays = (0 until noOfRays).map {
            Ray(epicentre, direction, distance).also {
                u.mul(direction)
            }
        }.toTypedArray()
    }

    /**
     * Updates all rays.
     *
     * @param worldBodies Arraylist of all bodies to update ray projections for.
     */
    fun updateRays(worldBodies: ArrayList<Body>) {
        for (ray in rays) {
            ray.updateProjection(worldBodies)
        }
    }

    /**
     * Debug draw method for rays and intersections.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    fun draw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera) {
        for (ray in rays) {
            ray.draw(g, paintSettings, camera)
        }
    }
}
