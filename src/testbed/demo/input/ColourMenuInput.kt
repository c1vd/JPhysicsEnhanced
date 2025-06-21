package testbed.demo.input

import testbed.demo.TestBedWindow
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ColourMenuInput(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), ActionListener {
    override fun actionPerformed(event: ActionEvent) {
        when (event.getActionCommand()) {
            "Default" -> TESTBED.PAINT_SETTINGS.defaultColourScheme()
            "Box2d" -> TESTBED.PAINT_SETTINGS.box2dColourScheme()
            "Monochromatic" -> TESTBED.PAINT_SETTINGS.monochromaticColourScheme()
            "Display Grid" -> TESTBED.PAINT_SETTINGS.drawGrid = !TESTBED.PAINT_SETTINGS.drawGrid
            "Display Shapes" -> TESTBED.PAINT_SETTINGS.drawShapes = !TESTBED.PAINT_SETTINGS.drawShapes
            "Display Joints" -> TESTBED.PAINT_SETTINGS.drawJoints = !TESTBED.PAINT_SETTINGS.drawJoints
            "Display AABBs" -> TESTBED.PAINT_SETTINGS.drawAABBs = !TESTBED.PAINT_SETTINGS.drawAABBs
            "Display Contacts" -> TESTBED.PAINT_SETTINGS.drawContacts = !TESTBED.PAINT_SETTINGS.drawContacts
            "Display COMs" -> TESTBED.PAINT_SETTINGS.drawCOMs = !TESTBED.PAINT_SETTINGS.drawCOMs
        }
    }
}