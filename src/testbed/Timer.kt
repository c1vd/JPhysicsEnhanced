package testbed

/**
 * Timer class.
 */
class Timer {
    var prevTime: Long = 0

    /**
     * No-argument constructor initializes instance variables to current time
     */
    init {
        reset()
    }

    /**
     * Resets instance values to current time
     */
    fun reset() {
        prevTime = System.nanoTime()
    }

    /**
     * Method to get the time difference between the prevTime and the current time.
     * @return long value with the time difference since prevTime was set
     */
    fun timePassed(): Long {
        return System.nanoTime() - prevTime
    }
}
