package testbed.demo

import testbed.demo.TestBedWindow.Companion.showWindow


fun main() {
    val demoWindow = TestBedWindow(true)
    showWindow(demoWindow, "2D Physics Engine Demo", 1280, 720)
    demoWindow.startThread()
}
