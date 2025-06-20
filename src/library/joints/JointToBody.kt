package library.joints

import library.dynamics.Body
import library.math.Matrix2D
import library.math.Vec2
import testbed.Camera
import testbed.ColourSettings
import java.awt.Graphics2D
import java.awt.geom.Line2D

/**
 * Class for a joint between two bodies.
 */
class JointToBody
/**
 * Constructor for a joint between two bodies.
 *
 * @param b1            First body the joint is attached to
 * @param object2            Second body the joint is attached to
 * @param jointLength   The desired distance of the joint between two points/bodies
 * @param jointConstant The strength of the joint
 * @param dampening     The dampening constant to use for the joints forces
 * @param canGoSlack    Boolean whether the joint can go slack or not
 * @param offset1       Offset to be applied to the location of the joint relative to b1's object space
 * @param offset2       Offset to be applied to the location of the joint relative to b2's object space
 */(
    b1: Body,
    private val object2: Body,
    jointLength: Double,
    jointConstant: Double,
    dampening: Double,
    canGoSlack: Boolean,
    offset1: Vec2,
    private val offset2: Vec2
) : Joint(b1, jointLength, jointConstant, dampening, canGoSlack, offset1) {
    private var object2AttachmentPoint: Vec2? = null

    /**
     * Applies tension to the two bodies.
     */
    override fun applyTension() {
        val mat1 = Matrix2D(object1.orientation)
        this.object1AttachmentPoint = object1.position + mat1.mul(offset1, Vec2())

        val mat2 = Matrix2D(object2.orientation)
        this.object2AttachmentPoint = object2.position + mat2.mul(offset2, Vec2())

        val tension = calculateTension()
        val distance = (this.object2AttachmentPoint!! - this.object1AttachmentPoint!!).normalized

        val impulse = distance.scalar(tension)
        object1.applyLinearImpulse(impulse, object1AttachmentPoint!! - object1.position)
        object2.applyLinearImpulse(-impulse, object2AttachmentPoint!! - object2.position)
    }

    /**
     * Calculates tension between the two attachment points of the joints bodies.
     *
     * @return double value of the tension force between the two bodies attachment points
     */
    override fun calculateTension(): Double {
        val distance = (object1AttachmentPoint!! - object2AttachmentPoint!!).length()
        if (distance < naturalLength && canGoSlack) {
            return 0.0
        }
        val extensionRatio = distance - naturalLength
        val tensionDueToHooksLaw = extensionRatio * springConstant
        val tensionDueToMotionDamping = dampeningConstant * rateOfChangeOfExtension()
        return tensionDueToHooksLaw + tensionDueToMotionDamping
    }

    /**
     * Determines the rate of change between two objects.
     *
     * @return double value of the rate of change
     */
    override fun rateOfChangeOfExtension(): Double {
        val distance = (object2AttachmentPoint!! - object1AttachmentPoint!!).normalized

        val relativeVelocity = object2.velocity - object1.velocity +
                (object2AttachmentPoint!! - object2.position).crossProduct(object2.angularVelocity) -
                (object1AttachmentPoint!! - object1.position).crossProduct(object1.angularVelocity)

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
        val obj1Pos = camera.convertToScreen(object1AttachmentPoint!!)
        val obj2Pos = camera.convertToScreen(object2AttachmentPoint!!)
        g.draw(Line2D.Double(obj1Pos.x, obj1Pos.y, obj2Pos.x, obj2Pos.y))
    }
}