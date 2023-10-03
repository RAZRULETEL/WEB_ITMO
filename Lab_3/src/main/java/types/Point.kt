package types

import java.io.Serializable

class Point : Serializable {
    var x: Float? = 0f
    var y: Float? = null
    var r: Float? = null

    fun setXIfInRange(
        x: Float,
        min: Float,
        max: Float,
    ) {
        if (x in min..max) this.x = x
    }

    fun getResult(nanoStart: Long): PointCheckResult {
        return PointCheckResult(x!!, y!!, r!!, System.currentTimeMillis(), nanoStart)
    }

    override fun toString(): String {
        return "{\"x\": $x, \"y\": $y, \"r\": $r}"
    }
}