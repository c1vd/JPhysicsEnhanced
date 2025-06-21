package library.dynamics

import java.util.*

/**
 * Settings class where all the constants are stored for the physics engine.
 */
object Settings {
    const val PENETRATION_ALLOWANCE: Double = 0.01
    const val PENETRATION_CORRECTION: Double = 0.5
    const val BIAS_RELATIVE: Double = 0.95
    const val BIAS_ABSOLUTE: Double = 0.01

    var HERTZ: Double = 60.0
    const val ITERATIONS: Int = 100
    const val EPSILON: Double = 1E-12

    /**
     * Generates a random number within the desired range.
     * @param min Minimum double value that the range can fall inside
     * @param max Maximum double value that the range can fall inside
     * @return double value inside the range of min and max supplied
     */
    fun generateRandomNoInRange(min: Double, max: Double): Double {
        val rand = Random()
        return min + (max - min) * rand.nextDouble()
    }

    /**
     * Generates a random number within the desired range.
     * @param min Minimum int value that the range can fall inside
     * @param max Maximum int value that the range can fall inside
     * @return int value inside the range of min and max supplied
     */
    fun generateRandomNoInRange(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }
}