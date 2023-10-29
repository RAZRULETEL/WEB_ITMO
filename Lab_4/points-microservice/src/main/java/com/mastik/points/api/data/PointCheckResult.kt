package com.mastik.points.api.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.annotation.processing.Generated
import kotlin.math.abs
import kotlin.math.sign

@Table("points")
class PointCheckResult(
    val x: Float,
    val y: Float,
    val r: Float,
    val timestamp: Long,
    val executionTime: Double,
    val success: Boolean,
) : Serializable {

    @Id
    var id: Int? = null

    constructor(
        x: Float,
        y: Float,
        r: Float,
        timestamp: Long,
        nanoTimeStart: Long,
    ) :
            this(
                x,
                y,
                r,
                timestamp,
                (System.nanoTime() - nanoTimeStart) / 1e6,
                checkHit(x * sign(r) , y * sign(r), abs(r))
            )

    override fun toString(): String {
        return "PointCheckResult(x=$x, y=$y, r=$r, timestamp=$timestamp, executionTime=$executionTime, success=$success)"
    }

    fun JSONString(): String{
        return "{\"x\": $x, \"y\": $y, \"r\": $r, \"timestamp\": $timestamp, \"executionTime\": $executionTime, \"success\": $success}"
    }

    companion object {
        private fun checkHit(
            x: Float,
            y: Float,
            r: Float
        ): Boolean {
            if (r == 0f) return false
            if (x > 0)
                if (y > 0)
                    return x * x + y * y <= (r * r)/4 // Arc
                else
                    return x <= r / 2 && y >= -r * (1 - x / (r / 2)) // Triangle
            else
                if (y > 0)
                    return false
                else
                    return x >= -r / 2 && y >= -r // Rectangle
        }
    }
}