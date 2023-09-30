package types

import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PointCheckResult (val x: Float, val y: Float, val r: Float, val timestamp: Long, val executionTime: Double, val success: Boolean):
    Serializable {


    constructor(x: Float, y: Float, r: Float, timestamp: Long, nanoTimeStart: Long):
            this(x, y, r, timestamp, (System.nanoTime() - nanoTimeStart) / 1e6, checkHit(x, y, r))

    fun getFormattedTimestamp(pattern: String): String{
        return DateTimeFormatter.ofPattern(pattern).format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()))
    }

    override fun toString(): String {
        return "{\"x\": $x, \"y\": $y, \"r\": $r, \"success\": $success, \"timestamp\": $timestamp, \"exec_time\": $executionTime}"
    }

    companion object{
        private fun checkHit(x: Float, y: Float, r: Float): Boolean {
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
    }
}