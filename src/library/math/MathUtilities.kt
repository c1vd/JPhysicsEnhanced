package library.math

fun isInfinite(x: Double): Boolean = x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY

fun isNaN(x: Double): Boolean = x != x