package testbed.demo.input

import testbed.demo.TestBedWindow
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ColourMenuInput(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), ActionListener {
    override fun actionPerformed(event: ActionEvent) {
        when (event.getActionCommand()) {
            "Default" -> TESTBED.pAINT_SETTINGS.defaultColourScheme()
            "Box2d" -> TESTBED.pAINT_SETTINGS.box2dColourScheme()
            "Monochromatic" -> TESTBED.pAINT_SETTINGS.monochromaticColourScheme()
            "Display Grid" -> TESTBED.pAINT_SETTINGS.drawGrid = !TESTBED.pAINT_SETTINGS.drawGrid
            "Display Shapes" -> TESTBED.pAINT_SETTINGS.drawShapes = !TESTBED.pAINT_SETTINGS.drawShapes
            "Display Joints" -> TESTBED.pAINT_SETTINGS.drawJoints = !TESTBED.pAINT_SETTINGS.drawJoints
            "Display AABBs" -> TESTBED.pAINT_SETTINGS.drawAABBs = !TESTBED.pAINT_SETTINGS.drawAABBs
            "Display Contacts" -> TESTBED.pAINT_SETTINGS.drawContacts = !TESTBED.pAINT_SETTINGS.drawContacts
            "Display COMs" -> TESTBED.pAINT_SETTINGS.drawCOMs = !TESTBED.pAINT_SETTINGS.drawCOMs
        }
    }
}