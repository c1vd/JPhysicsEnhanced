package testbed.demo

import library.collision.AABB.Companion.AABBOverLap
import library.dynamics.Body
import library.dynamics.Settings
import library.dynamics.Settings.generateRandomNoInRange
import library.dynamics.World
import library.explosions.Explosion
import library.explosions.ParticleExplosion
import library.geometry.Circle
import library.geometry.Polygon
import library.math.Vec2
import library.rays.Ray
import library.rays.ShadowCasting
import library.rays.Slice
import testbed.Camera
import testbed.ColourSettings
import testbed.DemoText.draw
import testbed.Trail
import testbed.demo.input.*
import testbed.demo.tests.Chains.load
import testbed.demo.tests.Raycast
import testbed.demo.tests.Raycast.action
import java.awt.*
import java.awt.event.*
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import javax.swing.*
import kotlin.concurrent.Volatile

class TestBedWindow(private val ANTIALIASING: Boolean) : JPanel(), Runnable {
    val camera: Camera

    fun setCamera(centre: Vec2, zoom: Double) {
        camera.setCentre(centre)
        camera.zoom = zoom
    }

    private val PHYSICS_THREAD: Thread = Thread(this)

    //Input handler classes
    private val KEY_INPUT: KeyBoardInput
    private val MOUSE_INPUT: MouseInput
    private val MOUSE_SCROLL_INPUT: MouseScroll
    private val MOUSE_MOTION_INPUT: MouseMotionListener

    fun startThread() {
        PHYSICS_THREAD.start()
    }

    private val rays = ArrayList<Ray>()

    fun add(ray: Ray?) {
        rays.add(ray!!)
    }

    val slices: ArrayList<Slice> = ArrayList<Slice>()

    fun add(s: Slice?) {
        slices.add(s!!)
    }

    val slicesSize: Int
        get() = slices.size

    val rayExplosions: ArrayList<Explosion> = ArrayList<Explosion>()

    fun add(ex: Explosion?) {
        rayExplosions.add(ex!!)
    }

    private val particles = ArrayList<ParticleExplosion?>()

    fun add(p: ParticleExplosion, lifespan: Double) {
        particles.add(p)
        for (b in p.particles) {
            trailsToBodies.add(Trail(1000, 1, b!!, lifespan))
        }
    }

    private val shadowCastings = ArrayList<ShadowCasting>()

    fun add(shadowCasting: ShadowCasting?) {
        shadowCastings.add(shadowCasting!!)
    }

    var world: World = World()

    private val trailsToBodies = ArrayList<Trail>()

    fun add(trail: Trail) {
        trailsToBodies.add(trail)
    }

    private var running = true

    @Volatile
    var isPaused: Boolean = false
        private set
    private val pauseLock = Any()

    fun stop() {
        running = false
        PHYSICS_THREAD.interrupt()
    }

    fun pause() {
        this.isPaused = true
    }

    fun resume() {
        synchronized(pauseLock) {
            this.isPaused = false
            (pauseLock as Object).notifyAll()
        }
    }

    private fun updateRays() {
        for (r in rays) {
            if (Raycast.active) {
                action(r)
            }
            r.updateProjection(world.bodies)
        }
        for (p in this.rayExplosions) {
            p.update(world.bodies)
        }
        for (s in shadowCastings) {
            s.updateProjections(world.bodies)
        }
        for (s in slices) {
            s.updateProjection(world.bodies)
        }
    }

    private fun updateTrails() {
        for (t in trailsToBodies) {
            t.updateTrail()
        }
    }

    override fun run() {
        while (running) {
            synchronized(pauseLock) {
                if (!running) {
                    return
                }
                if (this.isPaused) {
                    try {
                        synchronized(pauseLock) {
                            (pauseLock as Object).wait()
                        }
                    } catch (_: InterruptedException) {
                        return
                    }
                    if (!running) {
                        return
                    }
                }
            }
            repaint()
        }
    }

    private fun update() {
        val dt = if (Settings.HERTZ > 0.0) 1.0 / Settings.HERTZ else 0.0
        world.step(dt)
        updateTrails()
        updateRays()
        checkParticleLifetime(dt)
    }

    private fun checkParticleLifetime(timePassed: Double) {
        val bodiesToRemove = ArrayList<Body>()
        val i = trailsToBodies.iterator()
        while (i.hasNext()) {
            val s = i.next()
            if (s.checkLifespan(timePassed)) {
                bodiesToRemove.add(s.body)
                i.remove()
            }
        }
        val p = particles.iterator()
        while (p.hasNext()) {
            val s = p.next()!!.particles
            if (containsBody(s, bodiesToRemove)) {
                removeParticlesFromWorld(s)
                p.remove()
            }
        }
    }

    private fun removeParticlesFromWorld(s: Array<Body>) {
        for (b in s) {
            world.removeBody(b)
        }
    }

    private fun containsBody(s: Array<Body>, bodiesToRemove: ArrayList<Body>): Boolean {
        for (a in s) {
            if (bodiesToRemove.contains(a)) {
                return true
            }
        }
        return false
    }

    fun clearTestbedObjects() {
        camera.reset()
        world.clearWorld()
        trailsToBodies.clear()
        rays.clear()
        rayExplosions.clear()
        shadowCastings.clear()
        slices.clear()
        repaint()
    }


    val PAINT_SETTINGS: ColourSettings = ColourSettings()

    private var currentDemo = 0

    fun setCurrentDemo(i: Int) {
        currentDemo = i
    }

    var followPayload: Boolean = false

    init {

        val screenSize = Toolkit.getDefaultToolkit().screenSize
        this.camera = Camera(screenSize.getWidth().toInt(), screenSize.getHeight().toInt(), this)

        MOUSE_INPUT = MouseInput(this)
        addMouseListener(MOUSE_INPUT)

        KEY_INPUT = KeyBoardInput(this)
        addKeyListener(KEY_INPUT)

        MOUSE_SCROLL_INPUT = MouseScroll(this)
        addMouseWheelListener(MOUSE_SCROLL_INPUT)

        MOUSE_MOTION_INPUT = MouseMovement(this)
        addMouseMotionListener(MOUSE_MOTION_INPUT)

        load(this)
    }

    public override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        if (ANTIALIASING) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        setBackground(PAINT_SETTINGS.background)
        update()
        if (followPayload) {
            setCamera(Vec2(world.bodies[3].position.x, this.camera.centre.y), 2.0)
        }
        if (PAINT_SETTINGS.drawGrid) {
            drawGridMethod(g2d)
        }
        for (s in shadowCastings) {
            s.draw(g2d, this.PAINT_SETTINGS, this.camera)
        }
        drawTrails(g2d)
        for (b in world.bodies) {
            if (PAINT_SETTINGS.drawShapes) {
                b.shape.draw(g2d, this.PAINT_SETTINGS, this.camera)
            }
            if (PAINT_SETTINGS.drawAABBs) {
                b.shape.drawAABB(g2d, this.PAINT_SETTINGS, this.camera)
            }
            if (PAINT_SETTINGS.drawCOMs) {
                b.shape.drawCOMS(g2d, this.PAINT_SETTINGS, this.camera)
            }
        }
        if (PAINT_SETTINGS.drawContacts) {
            world.drawContact(g2d, this.PAINT_SETTINGS, this.camera)
        }
        if (PAINT_SETTINGS.drawJoints) {
            for (j in world.joints) {
                j.draw(g2d, this.PAINT_SETTINGS, this.camera)
            }
        }
        for (p in this.rayExplosions) {
            p.draw(g2d, this.PAINT_SETTINGS, this.camera)
        }
        for (r in rays) {
            r.draw(g2d, this.PAINT_SETTINGS, this.camera)
        }
        for (s in slices) {
            s.draw(g2d, this.PAINT_SETTINGS, this.camera)
        }
        draw(g2d, this.PAINT_SETTINGS, currentDemo)
    }

    private fun drawGridMethod(g2d: Graphics2D) {
        val projection = 20000
        val spacing = 10
        val minXY = -projection
        val maxXY = projection
        val totalProjectionDistance = projection + projection
        g2d.color = PAINT_SETTINGS.gridLines
        var i = 0
        while (i <= totalProjectionDistance) {
            if (i == projection) {
                g2d.stroke = PAINT_SETTINGS.axisStrokeWidth
                g2d.color = PAINT_SETTINGS.gridAxis
            }

            val currentMinY = camera.convertToScreen(Vec2((minXY + i).toDouble(), minXY.toDouble()))
            val currentMaxY = camera.convertToScreen(Vec2((minXY + i).toDouble(), maxXY.toDouble()))
            g2d.draw(Line2D.Double(currentMinY.x, currentMinY.y, currentMaxY.x, currentMaxY.y))

            val currentMinX = camera.convertToScreen(Vec2(minXY.toDouble(), (minXY + i).toDouble()))
            val currentMaxX = camera.convertToScreen(Vec2(maxXY.toDouble(), (minXY + i).toDouble()))
            g2d.draw(Line2D.Double(currentMinX.x, currentMinX.y, currentMaxX.x, currentMaxX.y))

            if (i == projection) {
                g2d.stroke = PAINT_SETTINGS.defaultStrokeWidth
                g2d.color = PAINT_SETTINGS.gridLines
            }
            i += spacing
        }
    }

    private fun drawTrails(g: Graphics2D) {
        g.color = PAINT_SETTINGS.trail
        for (t in trailsToBodies) {
            val s = Path2D.Double()
            for (i in t.trailPoints.indices) {
                var v = t.trailPoints[i]
                v = camera.convertToScreen(v)
                if (i == 0) {
                    s.moveTo(v.x, v.y)
                } else {
                    s.lineTo(v.x, v.y)
                }
            }
            g.draw(s)
        }
    }

    fun generateRandomObjects(lowerBound: Vec2, upperBound: Vec2, totalObjects: Int, maxRadius: Int) {
        var totalObjects = totalObjects
        while (totalObjects > 0) {
            val b = createRandomObject(lowerBound, upperBound, maxRadius)
            if (overlap(b!!)) {
                world.addBody(b)
                totalObjects--
            }
        }
    }

    fun generateBoxOfObjects() {
        run {
            val top = Body(Polygon(900.0, 20.0), -20.0, 500.0)
            top.setDensity(0.0)
            world.addBody(top)

            val right = Body(Polygon(500.0, 20.0), 900.0, 20.0)
            right.orientation = 1.5708
            right.setDensity(0.0)
            world.addBody(right)

            val bottom = Body(Polygon(900.0, 20.0), 20.0, -500.0)
            bottom.setDensity(0.0)
            world.addBody(bottom)

            val left = Body(Polygon(500.0, 20.0), -900.0, -20.0)
            left.orientation = 1.5708
            left.setDensity(0.0)
            world.addBody(left)
        }

        run {
            generateRandomObjects(Vec2(-880.0, -480.0), Vec2(880.0, 480.0), 30, 100)
            setStaticWorldBodies()
        }
    }

    private fun overlap(b: Body): Boolean {
        for (a in world.bodies) {
            if (AABBOverLap(a, b)) {
                return false
            }
        }
        return true
    }

    private fun createRandomObject(lowerBound: Vec2, upperBound: Vec2, maxRadius: Int): Body? {
        val objectType = generateRandomNoInRange(1, 2)
        var b: Body? = null
        val radius = generateRandomNoInRange(5, maxRadius)
        val x = generateRandomNoInRange(lowerBound.x + radius, upperBound.x - radius)
        val y = generateRandomNoInRange(lowerBound.y + radius, upperBound.y - radius)
        val rotation = generateRandomNoInRange(0.0, 7.0)
        when (objectType) {
            1 -> {
                b = Body(Circle(radius.toDouble()), x, y)
                b.orientation = rotation
            }

            2 -> {
                val sides = generateRandomNoInRange(3, 10)
                b = Body(Polygon(radius, sides), x, y)
                b.orientation = rotation
            }
        }
        return b
    }

    fun setStaticWorldBodies() {
        for (b in world.bodies) {
            b.setDensity(0.0)
        }
    }

    fun buildExplosionDemo() {
        run {
            buildShelf(50.0, 300.0)
            buildShelf(450.0, 400.0)
        }

        val floor = Body(Polygon(20000.0, 2000.0), 0.0, -2000.0)
        floor.setDensity(0.0)
        world.addBody(floor)

        val reflect = Body(Polygon(40.0, 5.0), -100.0, 330.0)
        reflect.orientation = 0.785398
        reflect.setDensity(0.0)
        world.addBody(reflect)

        run {
            val top = Body(Polygon(120.0, 10.0), 450.0, 210.0)
            top.setDensity(0.0)
            world.addBody(top)

            val side1 = Body(Polygon(100.0, 10.0), 340.0, 100.0)
            side1.orientation = 1.5708
            side1.setDensity(0.0)
            world.addBody(side1)

            val side2 = Body(Polygon(100.0, 10.0), 560.0, 100.0)
            side2.orientation = 1.5708
            side2.setDensity(0.0)
            world.addBody(side2)
            for (i in 0..3) {
                val box = Body(Polygon(20.0, 20.0), 450.0, (20 + (i * 40)).toDouble())
                world.addBody(box)
            }
        }

        for (k in 0..1) {
            for (i in 0..4) {
                val box = Body(Polygon(20.0, 20.0), (-600 + (k * 200)).toDouble(), (20 + (i * 40)).toDouble())
                world.addBody(box)
            }
        }
    }

    fun buildShelf(x: Double, y: Double) {
        val shelf = Body(Polygon(100.0, 10.0), x, y)
        shelf.setDensity(0.0)
        world.addBody(shelf)

        val boxes = 4
        for (i in 0..<boxes) {
            val box = Body(Polygon(10.0, 20.0), x, y + 30 + (i * 40))
            world.addBody(box)
        }
    }

    fun createPyramid(noOfPillars: Int, x: Int, y: Int) {
        var x = x
        var y = y
        val height = 30.0
        val width = 5.0
        x = (x + width).toInt()

        val widthOfTopPillar = height + height
        for (k in 0..<noOfPillars) {
            x = (x + height).toInt()

            val initialPillar = Body(Polygon(width + 2, height), x.toDouble(), y + height)
            addPillar(initialPillar)

            for (i in 0..<noOfPillars - k) {
                val rightPillar =
                    Body(Polygon(width + 2, height), x + widthOfTopPillar + (widthOfTopPillar * i), y + height)
                addPillar(rightPillar)

                val topPillar =
                    Body(Polygon(height, width), x + height + (i * widthOfTopPillar), y + widthOfTopPillar + width)
                addPillar(topPillar)
            }
            y = (y + (widthOfTopPillar + width + width)).toInt()
        }
    }

    fun createTower(floors: Int, x: Int, y: Int) {
        var x = x
        var y = y
        val height = 30.0
        val width = 5.0
        x = (x + width).toInt()

        val heightOfPillar = height + height
        val widthOfPillar = width + width
        repeat(floors) {
            val leftPillar = Body(Polygon(width, height), x.toDouble(), y + height)
            addPillar(leftPillar)

            val rightPillar = Body(Polygon(width, height), x + heightOfPillar - widthOfPillar, y + height)
            addPillar(rightPillar)

            val topPillar = Body(Polygon(height, width), x + height - width, y + heightOfPillar + width)
            addPillar(topPillar)
            y = (y + (heightOfPillar + width + width)).toInt()
        }
    }

    //Removing some boiler plate for create tower and Pyramid
    private fun addPillar(b: Body) {
        b.restitution = 0.2
        b.setDensity(0.2)
        world.addBody(b)
    }

    //Removes friction from the world
    fun setWorldIce() {
        for (b in world.bodies) {
            b.staticFriction = 0.0
            b.dynamicFriction = 0.0
        }
    }

    // Scaled friction by a passed ratio
    fun scaleWorldFriction(ratio: Double) {
        for (b in world.bodies) {
            b.staticFriction *= ratio
            b.dynamicFriction *= ratio
        }
    }

    companion object {
        @JvmStatic
        fun showWindow(gameScreen: TestBedWindow?, title: String?, windowWidth: Int, windowHeight: Int) {
            if (gameScreen != null) {
                val window = JFrame(title)
                window.apply {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
                    add(gameScreen)
                    minimumSize = Dimension(800, 600)
                    setPreferredSize(Dimension(windowWidth, windowHeight))
                    pack()
                    setLocationRelativeTo(null)
                }

                gameScreen.setFocusable(true)
                gameScreen.setOpaque(true)
                gameScreen.setBackground(gameScreen.PAINT_SETTINGS.background)

                val menuBar = JMenuBar()
                menuBar.apply {
                    add(createTestMenu(gameScreen))
                    add(createColourSchemeMenu(gameScreen))
                    add(createFrequencyMenu(gameScreen))
                    add(createDisplayMenu(gameScreen))
                }

                window.jMenuBar = menuBar

                window.isVisible = true
            }
        }

        private fun createDisplayMenu(gameScreen: TestBedWindow): Component {
            val drawOptions = JMenu("Graphics Options")

            val showGrid = JMenuItem("Display Grid")
            drawOptions.add(showGrid)
            showGrid.addActionListener(ColourMenuInput(gameScreen))

            val displayShapes = JMenuItem("Display Shapes")
            drawOptions.add(displayShapes)
            displayShapes.addActionListener(ColourMenuInput(gameScreen))

            val displayJoints = JMenuItem("Display Joints")
            drawOptions.add(displayJoints)
            displayJoints.addActionListener(ColourMenuInput(gameScreen))

            val displayAABBs = JMenuItem("Display AABBs")
            drawOptions.add(displayAABBs)
            displayAABBs.addActionListener(ColourMenuInput(gameScreen))

            val displayContactPoints = JMenuItem("Display Contacts")
            drawOptions.add(displayContactPoints)
            displayContactPoints.addActionListener(ColourMenuInput(gameScreen))

            val displayCOMs = JMenuItem("Display COMs")
            drawOptions.add(displayCOMs)
            displayCOMs.addActionListener(ColourMenuInput(gameScreen))

            return drawOptions
        }

        private fun createFrequencyMenu(gameScreen: TestBedWindow?): Component {
            val hertzMenu = JMenu("Hertz")
            val number = 30
            for (i in 1..4) {
                val hertzMenuItem = JMenuItem("" + number * i)
                hertzMenu.add(hertzMenuItem)
                hertzMenuItem.addActionListener { e: ActionEvent ->
                    when (e.getActionCommand()) {
                        "30" -> Settings.HERTZ = 30.0
                        "60" -> Settings.HERTZ = 60.0
                        "90" -> Settings.HERTZ = 90.0
                        "120" -> Settings.HERTZ = 120.0
                    }
                }
            }
            return hertzMenu
        }

        private fun createColourSchemeMenu(gameScreen: TestBedWindow): JMenu {
            val colourScheme = JMenu("Colour schemes")

            val defaultScheme = JMenuItem("Default")
            colourScheme.add(defaultScheme)
            defaultScheme.addActionListener(ColourMenuInput(gameScreen))

            val box2dScheme = JMenuItem("Box2d")
            colourScheme.add(box2dScheme)
            box2dScheme.addActionListener(ColourMenuInput(gameScreen))

            val monochromaticScheme = JMenuItem("Monochromatic")
            colourScheme.add(monochromaticScheme)
            monochromaticScheme.addActionListener(ColourMenuInput(gameScreen))

            return colourScheme
        }

        private fun createTestMenu(gameScreen: TestBedWindow): JMenu {
            val testMenu = JMenu("Demos")
            testMenu.setMnemonic(KeyEvent.VK_M)

            val chains = JMenuItem("Chains")
            chains.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK))
            testMenu.add(chains)
            chains.addActionListener(KeyBoardInput(gameScreen))

            val lineOfSight = JMenuItem("Line of sight")
            lineOfSight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_DOWN_MASK))
            testMenu.add(lineOfSight)
            lineOfSight.addActionListener(KeyBoardInput(gameScreen))

            val particleExplosion = JMenuItem("Particle explosion")
            particleExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_DOWN_MASK))
            testMenu.add(particleExplosion)
            particleExplosion.addActionListener(KeyBoardInput(gameScreen))

            val proximityExplosion = JMenuItem("Proximity explosion")
            proximityExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_DOWN_MASK))
            testMenu.add(proximityExplosion)
            proximityExplosion.addActionListener(KeyBoardInput(gameScreen))

            val raycastExplosion = JMenuItem("Raycast explosion")
            raycastExplosion.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.ALT_DOWN_MASK))
            testMenu.add(raycastExplosion)
            raycastExplosion.addActionListener(KeyBoardInput(gameScreen))

            val raycast = JMenuItem("Raycast")
            raycast.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.ALT_DOWN_MASK))
            testMenu.add(raycast)
            raycast.addActionListener(KeyBoardInput(gameScreen))

            val trebuchet = JMenuItem("Trebuchet")
            trebuchet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.ALT_DOWN_MASK))
            testMenu.add(trebuchet)
            trebuchet.addActionListener(KeyBoardInput(gameScreen))

            val sliceObjects = JMenuItem("Slice objects")
            sliceObjects.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.ALT_DOWN_MASK))
            testMenu.add(sliceObjects)
            sliceObjects.addActionListener(KeyBoardInput(gameScreen))

            val bouncingBall = JMenuItem("Bouncing ball")
            bouncingBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.ALT_DOWN_MASK))
            testMenu.add(bouncingBall)
            bouncingBall.addActionListener(KeyBoardInput(gameScreen))

            val mixedShapes = JMenuItem("Mixed shapes")
            mixedShapes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK))
            testMenu.add(mixedShapes)
            mixedShapes.addActionListener(KeyBoardInput(gameScreen))

            val newtonsCradle = JMenuItem("Newtons cradle")
            newtonsCradle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK))
            testMenu.add(newtonsCradle)
            newtonsCradle.addActionListener(KeyBoardInput(gameScreen))

            val wreckingBall = JMenuItem("Wrecking ball")
            wreckingBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK))
            testMenu.add(wreckingBall)
            wreckingBall.addActionListener(KeyBoardInput(gameScreen))

            val friction = JMenuItem("Friction")
            friction.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK))
            testMenu.add(friction)
            friction.addActionListener(KeyBoardInput(gameScreen))

            val drag = JMenuItem("Drag")
            drag.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK))
            testMenu.add(drag)
            drag.addActionListener(KeyBoardInput(gameScreen))

            val restitution = JMenuItem("Restitution")
            restitution.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.ALT_DOWN_MASK))
            testMenu.add(restitution)
            restitution.addActionListener(KeyBoardInput(gameScreen))

            val stackedObjects = JMenuItem("Stacked objects")
            stackedObjects.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK))
            testMenu.add(stackedObjects)
            stackedObjects.addActionListener(KeyBoardInput(gameScreen))

            return testMenu
        }
    }
}