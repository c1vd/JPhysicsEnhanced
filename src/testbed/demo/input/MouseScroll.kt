package testbed.demo.input

import testbed.demo.TestBedWindow
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

class MouseScroll(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), MouseWheelListener {
    override fun mouseWheelMoved(e: MouseWheelEvent) {
        if (e.getWheelRotation() < 0) {
            CAMERA.zoom *= 0.9
        } else {
            CAMERA.zoom *= 1.1
        }
    }
}
