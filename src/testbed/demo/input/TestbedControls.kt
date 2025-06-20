package testbed.demo.input

import library.dynamics.Settings
import library.explosions.ParticleExplosion
import library.explosions.ProximityExplosion
import library.math.Vec2
import testbed.Camera
import testbed.demo.TestBedWindow
import testbed.demo.tests.*
import java.awt.event.MouseEvent

abstract class TestbedControls(protected val TESTBED: TestBedWindow) {
    protected val CAMERA: Camera

    init {
        this.CAMERA = TESTBED.camera
    }

    fun loadDemo(demo: String) {
        currentDemo = demo
        TESTBED.clearTestbedObjects()
        resetUniqueEventHandlers()
        when (currentDemo) {
            "Chains" -> {
                Chains.load(TESTBED)
                TESTBED.setCurrentDemo(0)
            }

            "Line of sight" -> {
                LineOfSight.load(TESTBED)
                TESTBED.setCurrentDemo(1)
            }

            "Particle explosion" -> {
                ParticleExplosionTest.load(TESTBED)
                TESTBED.setCurrentDemo(2)
            }

            "Proximity explosion" -> {
                ProximityExplosionTest.load(TESTBED)
                TESTBED.setCurrentDemo(3)
            }

            "Raycast explosion" -> {
                RaycastExplosionTest.load(TESTBED)
                TESTBED.setCurrentDemo(4)
            }

            "Raycast" -> {
                Raycast.load(TESTBED)
                TESTBED.setCurrentDemo(5)
            }

            "Trebuchet" -> {
                Trebuchet.load(TESTBED)
                TESTBED.followPayload = true
                TESTBED.setCurrentDemo(6)
            }

            "Slice objects" -> {
                SliceObjects.load(TESTBED)
                Settings.HERTZ = 120.0
                TESTBED.setCurrentDemo(7)
            }

            "Bouncing ball" -> {
                BouncingBall.load(TESTBED)
                TESTBED.setCurrentDemo(8)
            }

            "Mixed shapes" -> {
                MixedShapes.load(TESTBED)
                TESTBED.setCurrentDemo(9)
            }

            "Newtons cradle" -> {
                NewtonsCradle.load(TESTBED)
                TESTBED.setCurrentDemo(10)
            }

            "Wrecking ball" -> {
                WreckingBall.load(TESTBED)
                TESTBED.setCurrentDemo(11)
            }

            "Friction" -> {
                Friction.load(TESTBED)
                TESTBED.setCurrentDemo(12)
            }

            "Drag" -> {
                Drag.load(TESTBED)
                TESTBED.setCurrentDemo(13)
            }

            "Restitution" -> {
                Restitution.load(TESTBED)
                TESTBED.setCurrentDemo(14)
            }

            "Stacked objects" -> {
                StackedObjects.load(TESTBED)
                TESTBED.setCurrentDemo(15)
            }
        }
    }

    private fun resetUniqueEventHandlers() {
        TESTBED.setCamera(Vec2(0.0, 0.0), 1.0)
        TESTBED.followPayload = false
        ProximityExplosionTest.active = false
        ParticleExplosionTest.active = false
        RaycastExplosionTest.active = false
        SliceObjects.active = false
        LineOfSight.active = false
        Trebuchet.active = false
        Settings.HERTZ = 60.0
    }

    protected fun setProximityEpicentre(e: MouseEvent) {
        val v = CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble()))
        val p = TESTBED.rayExplosions.get(0) as ProximityExplosion
        p.setEpicentre(v)
    }

    protected fun generateParticleExplosion(e: MouseEvent) {
        val p = ParticleExplosion(CAMERA.convertToWorld(Vec2(e.getX().toDouble(), e.getY().toDouble())), 100)
        p.createParticles(0.5, 100, 5, TESTBED.world)
        p.applyBlastImpulse(100.0)
        TESTBED.add(p, 2.0)
    }

    companion object {
        @JvmStatic
        protected var currentDemo: String = "Chains"
    }
}