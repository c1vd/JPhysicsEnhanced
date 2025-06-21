package testbed

import library.math.Vec2
import testbed.demo.TestBedWindow

class Camera(windowWidth: Int, windowHeight: Int, testWindow: TestBedWindow) {
    private val aspectRatio: Double
    var zoom: Double = 1.0
        set(zoom) {
            assert(zoom > 0)
            field = zoom
        }
    var width: Int
    var height: Int

    @JvmField
    var centre: Vec2
    private val panel: TestBedWindow

    var pointClicked: Vec2? = null

    var upperBound: Vec2 = Vec2()
    var lowerBound: Vec2 = Vec2()

    init {
        centre = Vec2(0.0, 0.0)
        zoom = 1.0
        this.width = windowWidth
        this.height = windowHeight
        panel = testWindow
        aspectRatio = width * 1.0 / height
    }

    fun convertToScreen(v: Vec2): Vec2 {
        updateViewSize(aspectRatio)
        val boxWidth = (v.x - lowerBound.x) / (upperBound.x - lowerBound.x)
        val boxHeight = (v.y - lowerBound.y) / (upperBound.y - lowerBound.y)

        val output = Vec2()
        output.x = boxWidth * panel.getWidth()
        output.y = (1.0 - boxHeight) * (panel.getWidth() / aspectRatio)
        return output
    }

    fun convertToWorld(vec: Vec2): Vec2 {
        updateViewSize(aspectRatio)
        val output = Vec2()
        val distAlongWindowXAxis = vec.x / panel.getWidth()
        output.x = (1.0 - distAlongWindowXAxis) * lowerBound.x + distAlongWindowXAxis * upperBound.x

        val aspectHeight = panel.getWidth() / aspectRatio
        val distAlongWindowYAxis = (aspectHeight - vec.y) / aspectHeight
        output.y = (1.0 - distAlongWindowYAxis) * lowerBound.y + distAlongWindowYAxis * upperBound.y
        return output
    }

    private fun updateViewSize(aspectRatio: Double) {
        var extents = Vec2(aspectRatio * 200, 200.0)
        extents = extents * zoom
        upperBound = centre + extents
        lowerBound = centre - extents
    }

    fun scaleToScreenXValue(radius: Double): Double {
        val aspectRatio = width * 1.0 / height
        var extents = Vec2(aspectRatio * 200, 200.0)
        extents = extents * zoom
        val upperBound = centre + extents
        val lowerBound = centre - extents
        val w = radius / (upperBound.x - lowerBound.x)
        return w * panel.getWidth()
    }

    fun transformCentre(v: Vec2) {
        centre.add(v)
    }

    fun setCentre(centre: Vec2) {
        this.centre = centre
    }

    fun reset() {
        setCentre(Vec2())
        zoom = 1.0
    }
}