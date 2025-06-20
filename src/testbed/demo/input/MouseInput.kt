package testbed.demo.input

import library.math.Vec2
import library.rays.Slice
import testbed.demo.TestBedWindow
import testbed.demo.tests.ParticleExplosionTest
import testbed.demo.tests.ProximityExplosionTest
import testbed.demo.tests.RaycastExplosionTest
import testbed.demo.tests.SliceObjects
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities

class MouseInput(testBedWindow: TestBedWindow) : TestbedControls(testBedWindow), MouseListener {
    override fun mouseClicked(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent) {
        if (SwingUtilities.isRightMouseButton(e)) {
            CAMERA.pointClicked = CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble()))
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (!SwingUtilities.isRightMouseButton(e)) {
            if (ProximityExplosionTest.active) {
                setProximityEpicentre(e)
                ProximityExplosionTest.p!!.applyBlastImpulse(5000000.0)
            } else if (ParticleExplosionTest.active) {
                generateParticleExplosion(e)
            } else if (RaycastExplosionTest.active) {
                RaycastExplosionTest.r!!.applyBlastImpulse(500000.0)
            } else if (SliceObjects.active) {
                if (TESTBED.slicesSize == 1) {
                    TESTBED.slices.get(0)
                        .setDirection(CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble())))
                    TESTBED.slices.get(0).sliceObjects(TESTBED.world)
                    TESTBED.slices.clear()
                } else {
                    val s = Slice(
                        CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble())),
                        Vec2(1.0, 0.0),
                        0.0
                    )
                    TESTBED.add(s)
                }
            }
        }
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }
}

