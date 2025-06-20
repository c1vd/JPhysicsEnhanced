package library.explosions

import library.dynamics.Body
import library.math.Vec2
import library.rays.RayInformation
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D

/**
 * Models raycast explosions.
 */
class RaycastExplosion(epicentre: Vec2, noOfRays: Int, distance: Int, worldBodies: ArrayList<Body>) : Explosion {
    private val rayScatter: RayScatter = RayScatter(epicentre, noOfRays)

    /**
     * Sets the epicentre to a different coordinate.
     *
     * @param v The vector position of the new epicentre.
     */
    override fun setEpicentre(v: Vec2) {
        rayScatter.setEpicentre(v)
    }

    var raysInContact: ArrayList<RayInformation> = ArrayList<RayInformation>()

    /**
     * Constructor
     *
     * @param epicentre   The epicentre of the explosion.
     * @param noOfRays    Number of projected rays.
     * @param distance    Distance of projected rays.
     * @param worldBodies The world the rays effect and are projected in.
     */
    init {
        rayScatter.castRays(distance)
        update(worldBodies)
    }

    /**
     * Updates the arraylist to reevaluate what objects are effected/within the proximity.
     *
     * @param bodiesToEvaluate Arraylist of bodies in the world to check.
     */
    override fun update(bodiesToEvaluate: ArrayList<Body>) {
        raysInContact.clear()
        rayScatter.updateRays(bodiesToEvaluate)
        val rayArray = rayScatter.rays
        for (ray in rayArray) {
            val rayInfo = ray.rayInformation
            if (rayInfo != null) {
                raysInContact.add(rayInfo)
            }
        }
    }

    /**
     * Applies a blast impulse to the effected bodies.
     *
     * @param blastPower The impulse magnitude.
     */
    override fun applyBlastImpulse(blastPower: Double) {
        for (ray in raysInContact) {
            val blastDir = ray.coord - rayScatter.getEpicentre()
            val distance = blastDir.length()
            if (distance == 0.0) return

            val invDistance = 1 / distance
            val impulseMag = blastDir.normalized.scalar(blastPower * invDistance)
            val b = ray.b
            b!!.applyLinearImpulse(impulseMag, ray.coord - b.position)
        }
    }

    /**
     * Debug draw method for all rays projected.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    override fun draw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera) {
        rayScatter.draw(g, paintSettings, camera)
    }
}

