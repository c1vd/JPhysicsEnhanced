package library.math

import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 2D Vectors class
 */
class Vec2 {

    var x: Double

    var y: Double

    /**
     * Default constructor - x/y initialised to zero.
     */
    constructor() {
        this.x = 0.0
        this.y = 0.0
    }

    /**
     * Constructor.
     *
     * @param x Sets x value.
     * @param y Sets y value.
     */
    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    /**
     * Copy constructor.
     *
     * @param vector Vector to copy.
     */
    constructor(vector: Vec2) {
        this.x = vector.x
        this.y = vector.y
    }

    /**
     * Constructs a normalised direction vector.
     *
     * @param direction Direction in radians.
     */
    constructor(direction: Double) {
        this.x = cos(direction)
        this.y = sin(direction)
    }

    /**
     * Sets a vector to equal an x/y value and returns this.
     *
     * @param x x value.
     * @param y y value.
     * @return The current instance vector.
     */
    fun set(x: Double, y: Double): Vec2 {
        this.x = x
        this.y = y
        return this
    }

    /**
     * Sets a vector to another vector and returns this.
     *
     * @param v1 Vector to set x/y values to.
     * @return The current instance vector.
     */
    fun set(v1: Vec2): Vec2 {
        this.x = v1.x
        this.y = v1.y
        return this
    }

    /**
     * Copy method to return a new copy of the current instance vector.
     *
     * @return A new Vectors2D object.
     */
    fun copy(): Vec2 {
        return Vec2(this.x, this.y)
    }

    /**
     * Negates the current instance vector and return this.
     *
     * @return Return the negative form of the instance vector.
     */
    fun negative(): Vec2 {
        this.x = -x
        this.y = -y
        return this
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-x, -y)
    }

    /**
     * Adds a vector to the current instance and return this.
     *
     * @param v Vector to add.
     * @return Returns the current instance vector.
     */
    fun add(v: Vec2): Vec2 {
        x += v.x
        y += v.y
        return this
    }


    /**
     * Generates a normal of a vector. Normal facing to the right clock wise 90 degrees.
     *
     * @return A normal of the current instance vector.
     */
    fun normal(): Vec2 {
        return Vec2(-y, x)
    }

    /**
     * Normalizes the current instance vector to length 1 and returns this.
     *
     * @return Returns the normalized version of the current instance vector.
     */
    fun normalize(): Vec2 {
        var d = sqrt(x * x + y * y)
        if (d == 0.0) {
            d = 1.0
        }
        this.x /= d
        this.y /= d
        return this
    }

    val normalized: Vec2
        /**
         * Finds the normalised version of a vector and returns a new vector of it.
         *
         * @return A normalized vector of the current instance vector.
         */
        get() {
            var d = sqrt(x * x + y * y)

            if (d == 0.0) {
                d = 1.0
            }
            return Vec2(x / d, y / d)
        }

    /**
     * Finds the distance between two vectors.
     *
     * @param v Vector to find distance from.
     * @return Returns distance from vector v to the current instance vector.
     */
    fun distance(v: Vec2): Double {
        val dx = this.x - v.x
        val dy = this.y - v.y
        return StrictMath.sqrt(dx * dx + dy * dy)
    }

    operator fun plus(v: Vec2): Vec2 {
        return Vec2(this.x + v.x, this.y + v.y)
    }

    operator fun minus(v: Vec2): Vec2 {
        return Vec2(this.x - v.x, this.y - v.y)
    }

    /**
     * Finds cross product between two vectors.
     *
     * @param v Other vector to apply cross product to
     * @return double
     */
    fun cross(v: Vec2): Double {
        return this.x * v.y - this.y * v.x
    }

    fun cross(a: Double): Vec2 {
        return this.normal() * a
    }

    operator fun times(a: Double): Vec2 {
        return Vec2(x * a, y * a)
    }

    /**
     * Finds dotproduct between two vectors.
     *
     * @param v Other vector to apply dotproduct to.
     * @return Double
     */
    fun dotProduct(v: Vec2): Double {
        return this.x * v.x + this.y * v.y
    }

    val length: Double
        get() = sqrt(x * x + y * y)

    val isValid: Boolean
        /**
         * Checks to see if a vector has valid values set for x and y.
         *
         * @return boolean value whether a vector is valid or not.
         */
        get() = !isNaN(x) && !isInfinite(x) && !isNaN(y) && !isInfinite(y)

    val isZero: Boolean
        /**
         * Checks to see if a vector is set to (0,0).
         *
         * @return boolean value whether the vector is set to (0,0).
         */
        get() = x == 0.0 && y == 0.0

    override fun toString(): String {
        return this.x.toString() + " : " + this.y
    }

    companion object {

        /**
         * Generates an array of length n with zero initialised vectors.
         *
         * @param n Length of array.
         * @return A Vectors2D array of zero initialised vectors.
         */
        fun createArray(n: Int): Array<Vec2> {
            return Array(n) { Vec2() }
        }
    }
}
