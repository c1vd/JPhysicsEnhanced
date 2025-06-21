package testbed.demo.input

import library.explosions.ProximityExplosion
import library.math.Vec2
import testbed.demo.TestBedWindow
import testbed.demo.tests.LineOfSight
import testbed.demo.tests.ProximityExplosionTest
import testbed.demo.tests.RaycastExplosionTest
import testbed.demo.tests.SliceObjects
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.SwingUtilities

class MouseMovement(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), MouseMotionListener {
    override fun mouseDragged(e: MouseEvent) {
        if (SwingUtilities.isRightMouseButton(e)) {
            val pw = CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble()))
            val diff = pw - CAMERA.pointClicked!!
            CAMERA.setCentre(CAMERA.centre - diff)
        } else {
            val v = CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble()))
            if (ProximityExplosionTest.active) {
                val p = TESTBED.rayExplosions[0] as ProximityExplosion
                p.setEpicentre(v)
            } else if (RaycastExplosionTest.active) {
                RaycastExplosionTest.r!!.setEpicentre(v)
            } else if (LineOfSight.active) {
                LineOfSight.b!!.setStartPoint(v)
            } else if (TESTBED.slicesSize == 1 && !SwingUtilities.isRightMouseButton(e) && SliceObjects.active) {
                TESTBED.slices[0].setDirection(v)
            }
        }
    }

    override fun mouseMoved(e: MouseEvent) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            val v = CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble()))
            if (ProximityExplosionTest.active) {
                val p = TESTBED.rayExplosions[0] as ProximityExplosion
                p.setEpicentre(v)
            } else if (RaycastExplosionTest.active) {
                RaycastExplosionTest.r!!.setEpicentre(v)
            } else if (LineOfSight.active) {
                LineOfSight.b!!.setStartPoint(v)
            } else if (TESTBED.slicesSize == 1 && !SwingUtilities.isRightMouseButton(e) && SliceObjects.active) {
                TESTBED.slices.get(0).setDirection(v)
            }
        }
    }
}
