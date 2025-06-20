package library.explosions

import library.dynamics.Body
import library.dynamics.World
import library.geometry.Circle
import library.math.Matrix2D
import library.math.Vec2

/**
 * Models particle explosions.
 */
class ParticleExplosion(private val epicentre: Vec2, private val noOfParticles: Int) {
    /**
     * Getter to return the list of particles in the world.
     *
     * @return Array of bodies.
     */
    val particles: Array<Body?> = arrayOfNulls(noOfParticles)

    /**
     * Creates particles in the supplied world.
     *
     * @param size    The size of the particles.
     * @param density The density of the particles.
     * @param radius  The distance away from the epicenter the particles are placed.
     * @param world   The world the particles are created in.
     */
    fun createParticles(size: Double, density: Int, radius: Int, world: World) {
        val separationAngle = 6.28319 / noOfParticles
        val distanceFromCentre = Vec2(0.0, radius.toDouble())
        val rotate = Matrix2D(separationAngle)
        for (i in 0..<noOfParticles) {
            val particlePlacement = epicentre + distanceFromCentre
            val b = Body(Circle(size), particlePlacement.x, particlePlacement.y)
            b.setDensity(density.toDouble())
            b.restitution = 1.0
            b.staticFriction = 0.0
            b.dynamicFriction = 0.0
            b.affectedByGravity = false
            b.linearDampening = 0.0
            b.particle = true
            world.addBody(b)
            particles[i] = b
            rotate.mul(distanceFromCentre)
        }
    }

    /**
     * Applies a blast impulse to all particles created.
     *
     * @param blastPower The impulse magnitude.
     */
    fun applyBlastImpulse(blastPower: Double) {
        var line: Vec2
        for (b in particles) {
            line = b!!.position - epicentre
            b.velocity.set(line.scalar(blastPower))
        }
    }
}
