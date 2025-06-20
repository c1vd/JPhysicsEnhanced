package testbed.demo.input

import library.dynamics.Settings
import testbed.demo.TestBedWindow
import testbed.demo.tests.Trebuchet
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyBoardInput(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), KeyListener, ActionListener {
    override fun keyTyped(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (TESTBED.isPaused) {
                TESTBED.resume()
            } else {
                TESTBED.pause()
            }
        } else if (e.getKeyCode() == KeyEvent.VK_B) {
            if (TESTBED.world.joints.size == 3 && Trebuchet.active) {
                TESTBED.world.joints.removeAt(2)
                Settings.HERTZ = 60.0
            }
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            loadDemo(TestbedControls.Companion.currentDemo)
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.getKeyCode() == KeyEvent.VK_M) {
            val p = TESTBED.pAINT_SETTINGS
            p.drawText = !p.drawText
        }
    }

    override fun actionPerformed(event: ActionEvent) {
        loadDemo(event.getActionCommand())
    }
}

