package library.joints

import library.dynamics.Body
import library.math.Matrix2D
import library.math.Vec2
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D
import java.awt.geom.Line2D

/**
 * Class for a joint between a body and a point in world space.
 */
class JointToPoint
/**
 * Convenience constructor that works like
 * [.JointToPoint]
 *
 * @param pointAttachedTo         The point the joint is attached to
 * @param b1            First body the joint is attached to
 * @param jointLength   The desired distance of the joint between two points/bodies
 * @param jointConstant The strength of the joint
 * @param dampening     The dampening constant to use for the joints forces
 * @param canGoSlack    Boolean whether the joint can go slack or not
 * @param offset1       Offset to be applied to the location of the joint relative to b1's object space
 */(
    b1: Body,
    private val pointAttachedTo: Vec2,
    jointLength: Double,
    jointConstant: Double,
    dampening: Double,
    canGoSlack: Boolean,
    offset1: Vec2
) : Joint(b1, jointLength, jointConstant, dampening, canGoSlack, offset1) {
    /**
     * Constructor for a joint between a body and a point.
     *
     * @param point         The point the joint is attached to
     * @param b1            First body the joint is attached to
     * @param jointLength   The desired distance of the joint between two points/bodies
     * @param jointConstant The strength of the joint
     * @param dampening     The dampening constant to use for the joints forces
     * @param canGoSlack    Boolean whether the joint can go slack or not
     * @param offset1       Offset to be applied to the location of the joint relative to b1's object space
     */
    constructor(
        point: Vec2,
        b1: Body,
        jointLength: Double,
        jointConstant: Double,
        dampening: Double,
        canGoSlack: Boolean,
        offset1: Vec2
    ) : this(b1, point, jointLength, jointConstant, dampening, canGoSlack, offset1)

    /**
     * Applies tension to the body attached to the joint.
     */
    override fun applyTension() {
        val mat1 = Matrix2D(object1.orientation)
        object1AttachmentPoint = object1.position + mat1.mul(offset1, Vec2())

        val tension = calculateTension()
        val distance = (pointAttachedTo - object1AttachmentPoint).normalized

        val impulse = distance * tension
        object1.applyLinearImpulse(impulse, object1AttachmentPoint - object1.position)
    }

    /**
     * Calculates tension between the two attachment points of the joints body and point.
     *
     * @return double value of the tension force between the point and attached bodies point
     */
    override fun calculateTension(): Double {
        val distance = (object1AttachmentPoint - pointAttachedTo).length
        if (distance < naturalLength && canGoSlack) {
            return 0.0
        }
        val extensionRatio = distance - naturalLength
        val tensionDueToHooksLaw = extensionRatio * springConstant
        val tensionDueToMotionDamping = dampeningConstant * rateOfChangeOfExtension()
        return tensionDueToHooksLaw + tensionDueToMotionDamping
    }

    /**
     * Determines the rate of change between the attached point and body.
     *
     * @return double value of the rate of change
     */
    override fun rateOfChangeOfExtension(): Double {
        val distance = (pointAttachedTo - object1AttachmentPoint).normalized

        val relativeVelocity =
            -object1.velocity - object1AttachmentPoint - object1.position.cross(object1.angularVelocity)

        return relativeVelocity.dotProduct(distance)
    }

    /**
     * Implementation of the draw method.
     *
     * @param g             Graphics2D object to draw to
     * @param paintSettings Colour settings to draw the objects to screen with
     * @param camera        Camera class used to convert points from world space to view space
     */
    override fun draw(g: Graphics2D, paintSettings: ColourSettings, camera: Camera) {
        g.color = paintSettings.joints
        val obj1Pos = camera.convertToScreen(object1AttachmentPoint)
        val obj2Pos = camera.convertToScreen(pointAttachedTo)
        g.draw(Line2D.Double(obj1Pos.x, obj1Pos.y, obj2Pos.x, obj2Pos.y))
    }
}
