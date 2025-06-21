package testbed

import library.dynamics.Settings
import testbed.demo.tests.*
import testbed.demo.tests.LineOfSight.drawInfo
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D

object DemoText {
    @JvmStatic
    fun draw(g: Graphics2D, paintSettings: ColourSettings, demo: Int) {
        if (!paintSettings.drawText) {
            return
        }
        g.color = Color.white
        g.font = Font("Calibri", Font.PLAIN, 20)
        when (demo) {
            0 -> drawArray(Chains.text, g)
            1 -> drawArray(LineOfSight.text, g)
            2 -> drawArray(ParticleExplosionTest.text, g)
            3 -> drawArray(ProximityExplosionTest.text, g)
            4 -> drawArray(RaycastExplosionTest.text, g)
            5 -> drawArray(Raycast.text, g)
            6 -> drawArray(Trebuchet.text, g)
            7 -> drawArray(SliceObjects.text, g)
            8 -> drawArray(BouncingBall.text, g)
            9 -> drawArray(MixedShapes.text, g)
            10 -> drawArray(NewtonsCradle.text, g)
            11 -> drawArray(WreckingBall.text, g)
            12 -> drawArray(Friction.text, g)
            13 -> drawArray(Drag.text, g)
            14 -> drawArray(Restitution.text, g)
            15 -> drawArray(StackedObjects.text, g)
        }
    }

    fun drawArray(lines: Array<String?>, g: Graphics2D) {
        var y = 20
        for (line in lines) {
            g.drawString(line!!, 5, y)
            y += 20
        }
        g.drawString("Right click: moves the camera position", 5, y)
        g.drawString("Space: pauses demo", 5, 20.let { y += it; y })
        g.drawString("R: restart current demo", 5, 20.let { y += it; y })
        g.drawString("Hertz: " + Settings.HERTZ, 5, 20.let { y += it; y })
        if (LineOfSight.active) {
            drawInfo(g, 5, 20.let { y += it; y })
        }
    }
}
