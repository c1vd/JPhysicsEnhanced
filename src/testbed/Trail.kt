package testbed

import library.dynamics.Body
import library.math.Vec2
import library.math.Vec2.Companion.createArray

class Trail(noOfTrailPoints: Int, private val skipInterval: Int, @JvmField val body: Body, private val lifeSpan: Double) {

    val trailPoints: Array<Vec2> = createArray(noOfTrailPoints)
    private val arrayEndPos: Int = noOfTrailPoints - 1
    private var counter = 0
    private var timeActive = 0.0

    private var trailEndPointIndex = 0

    fun updateTrail() {
        if (counter >= skipInterval) {
            if (trailEndPointIndex <= arrayEndPos) {
                trailPoints[trailEndPointIndex] = body.position.copy()
                trailEndPointIndex++
            } else {
                System.arraycopy(trailPoints, 1, trailPoints, 0, arrayEndPos)
                trailPoints[arrayEndPos] = body.position.copy()
            }
            counter = 0
        } else {
            counter++
        }
    }

    fun checkLifespan(p: Double): Boolean {
        if (lifeSpan == 0.0) return false
        timeActive += p
        return timeActive > lifeSpan
    }
}