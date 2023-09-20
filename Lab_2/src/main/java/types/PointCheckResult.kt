package types


class PointCheckResult(val x: Float, val y: Float, val r: Float, val timestamp: Long, nanoTimeStart: Long) {
    val success: Boolean
    val executionTime: Double

    init {
        success = checkHit()
        executionTime = (System.nanoTime() - nanoTimeStart) / 1e6
        val thread = Thread();
    }

    private fun checkHit(): Boolean {
        if(x > 0)
            if(y > 0)
                return (x + y) <= r // Triangle
            else
                return x <= r && y >= -r/2 // Rectangle
        else
            if (y > 0)
                return false
            else
                return x * x + y * y <= r * r // Arc
    }

    override fun toString(): String {
        return "{\"x\": $x, \"y\": $y, \"r\": $r, \"success\": $success, \"timestamp\": $timestamp, \"exec_time\": $executionTime}"
    }
}