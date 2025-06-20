package library.dynamics

import library.collision.AABB
import library.geometry.Shape
import library.math.Vec2

/**
 * Class to create a body to add to a world.
 */
class Body(var shape: Shape, x: Double, y: Double) {

    var dynamicFriction: Double = 0.2

    var staticFriction: Double = 0.5

    var position: Vec2

    var velocity: Vec2

    var force: Vec2


    var angularVelocity: Double = 0.0

    var torque: Double = 0.0


    var restitution: Double = 0.8

    var mass: Double = 0.0

    var invMass: Double = 0.0

    var I: Double = 0.0
    var invI: Double = 0.0


    var orientation: Double = 0.0
        set(delta){
            field = delta
            shape.orient.set(orientation)
            shape.createAABB()
        }


    lateinit var aabb: AABB


    var linearDampening: Double = 0.0

    var affectedByGravity: Boolean

    var particle: Boolean

    /**
     * Constructor for body.
     *
     * @param shape Shape to bind to body.
     * @param x     Position x in world space.
     * @param y     Position y in world space.
     */
    init {
        this.shape.body = this

        position = Vec2(x, y)
        velocity = Vec2(0.0, 0.0)
        force = Vec2(0.0, 0.0)

        shape.orient.set(orientation)

        this.shape.calcMass(1.0)
        this.shape.createAABB()

        particle = false
        affectedByGravity = true
    }

    /**
     * Applies force ot body.
     *
     * @param force        Force vector to apply.
     * @param contactPoint The point to apply the force to relative to the body in object space.
     */
    fun applyForce(force: Vec2, contactPoint: Vec2) {
        this.force.add(force)
        torque += contactPoint.crossProduct(force)
    }

    /**
     * Apply force to the center of mass.
     *
     * @param force Force vector to apply.
     */
    fun applyForceToCentre(force: Vec2) {
        this.force.add(force)
    }

    /**
     * Applies impulse to a point relative to the body's center of mass.
     *
     * @param impulse      Magnitude of impulse vector.
     * @param contactPoint The point to apply the force to relative to the body in object space.
     */
    fun applyLinearImpulse(impulse: Vec2, contactPoint: Vec2) {
        velocity.add(impulse.scalar(invMass))
        angularVelocity += invI * contactPoint.crossProduct(impulse)
    }

    /**
     * Applies impulse to body's center of mass.
     *
     * @param impulse Magnitude of impulse vector.
     */
    fun applyLinearImpulseToCentre(impulse: Vec2) {
        velocity.add(impulse.scalar(invMass))
    }


    /**
     * Sets the density of the body's mass.
     *
     * @param density double value of desired density.
     */
    fun setDensity(density: Double) {
        if (density > 0.0) {
            shape.calcMass(density)
        } else {
            setStatic()
        }
    }

    /**
     * Sets all mass and inertia variables to zero. Object cannot be moved.
     */
    private fun setStatic() {
        mass = 0.0
        invMass = 0.0
        I = 0.0
        invI = 0.0
    }
}
