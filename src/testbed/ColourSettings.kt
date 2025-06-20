package testbed

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Stroke

class ColourSettings {
    fun defaultColourScheme() {
        background = Color(8, 20, 30, 255)
        shapeFill = Color(97, 146, 58, 255)
        shapeOutLine = Color(201, 206, 167, 255)
        staticFill = Color(97, 60, 60, 255)
        staticOutLine = Color(42, 28, 30, 255)

        aabb = Color(0, 255, 255, 255)
        joints = Color(0, 255, 255, 255)

        contactPoint = Color(255, 255, 255, 255)
        centreOfMass = Color(8, 20, 30, 255)
        trail = Color(255, 255, 0, 200)

        proximity = Color(255, 255, 0, 200)
        linesToObjects = Color(255, 255, 0, 180)
        rayToBody = Color(255, 255, 0, 255)
        projectedRay = Color(127, 127, 127, 100)
        scatterRays = Color(255, 255, 0, 255)
    }

    fun box2dColourScheme() {
        background = Color(0, 0, 0, 255)
        shapeFill = Color(57, 44, 44, 255)
        shapeOutLine = Color(229, 178, 178, 255)
        staticFill = Color(33, 57, 29, 255)
        staticOutLine = Color(124, 230, 129, 255)

        aabb = Color(255, 255, 255, 255)
        joints = Color(127, 204, 204, 255)

        contactPoint = Color(255, 255, 255, 255)
        centreOfMass = Color(231, 178, 177, 255)
        trail = Color(255, 255, 0, 200)

        proximity = Color(255, 255, 0, 200)
        linesToObjects = Color(255, 255, 0, 100)
        rayToBody = Color(255, 255, 255, 255)
        projectedRay = Color(127, 127, 127, 150)
        scatterRays = Color(255, 255, 0, 255)
    }

    fun monochromaticColourScheme() {
        background = Color(0, 0, 0, 255)
        shapeFill = Color(0, 0, 0, 255)
        shapeOutLine = Color(255, 255, 255, 255)
        staticFill = shapeFill
        staticOutLine = shapeOutLine

        aabb = Color(255, 255, 255, 255)
        joints = Color(255, 255, 255, 255)

        contactPoint = Color(255, 255, 255, 255)
        centreOfMass = Color(255, 255, 255, 255)
        trail = Color(255, 255, 255, 255)

        proximity = Color(255, 255, 255, 255)
        linesToObjects = Color(255, 255, 255, 255)
        rayToBody = Color(255, 255, 255, 255)
        projectedRay = Color(127, 127, 127, 100)
        scatterRays = Color(255, 255, 255, 255)
    }

    //All objects
    var aabb: Color? = null
    var centreOfMass: Color? = null
    var contactPoint: Color? = null
    @JvmField
    var trail: Color? = null
    val NORMAL_LINE_SCALAR: Double = 2.0
    val COM_RADIUS: Int = 5
    val TANGENT_LINE_SCALAR: Double = 2.0

    //Static objects
    var staticFill: Color? = null
    var staticOutLine: Color? = null

    //Non static objects
    var shapeFill: Color? = null
    var shapeOutLine: Color? = null
    var joints: Color? = null

    //Proximity explosion
    var proximity: Color? = null
    var linesToObjects: Color? = null
    val CIRCLE_RADIUS: Int = 3

    //Rays
    var rayToBody: Color? = null
    var projectedRay: Color? = null
    var scatterRays: Color? = null
    val RAY_DOT: Double = 2.0
    val shadow: Color = Color(128, 128, 128, 126)


    //Testbed related drawing
    @JvmField
    val gridLines: Color = Color(255, 255, 255, 20)
    @JvmField
    val gridAxis: Color = Color(255, 255, 255, 130)
    @JvmField
    var background: Color? = null
    @JvmField
    var axisStrokeWidth: Stroke = BasicStroke(2f)
    @JvmField
    var defaultStrokeWidth: Stroke = BasicStroke(1f)

    //Flags for testbed
    @JvmField
    var drawShapes: Boolean = true
    @JvmField
    var drawJoints: Boolean = true
    @JvmField
    var drawAABBs: Boolean = false
    @JvmField
    var drawContacts: Boolean = false
    @JvmField
    var drawCOMs: Boolean = false
    @JvmField
    var drawGrid: Boolean = false
    var drawText: Boolean = true

    init {
        defaultColourScheme()
    }
}