package com.mastik.points.api

import com.mastik.points.api.data.PointCheckResult
import com.mastik.points.api.data.PointDAO
import com.mastik.points.api.data.RawPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PointsController {

    @Autowired
    var pointsRepository: PointDAO? = null

    @PostMapping("/check")
    fun checkPoint(@RequestBody point: RawPoint): ResponseEntity<Any> {
        val nano = System.nanoTime()
        if (point.x == null) return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(constructJSONResponse(false, "required parameter x"))
        if (point.y == null) return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(constructJSONResponse(false, "required parameter y"))
        if (point.r == null) return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(constructJSONResponse(false, "required parameter r"))

        val res = PointCheckResult(point.x!!, point.y!!, point.r!!, System.currentTimeMillis(), nano)
        pointsRepository!!.save(res)
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(constructJSONResponse(true, res.JSONString() as Any))
    }

    @RequestMapping("/clear")
    fun clear(): ResponseEntity<Any>{
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(constructJSONResponse(true, "successfully cleared ${pointsRepository!!.clearAll()} elements"))
    }

    @GetMapping("/get")
    fun getAll(): ResponseEntity<Any>{
        val points = pointsRepository!!.getAll()
        val resp = StringBuilder("[")
        points.forEach { resp.append(it.JSONString()).append(", ") }
        val indx = resp.lastIndexOf(",")
        if(indx >= 0) resp[indx] = ']' else resp.append(']')
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(constructJSONResponse(true, resp.toString() as Any))
    }

    fun constructJSONResponse(success: Boolean, result: Any): String {
        return "{\"success\": ${if (success) 1 else 0}, ${if (!success) "\"error\"" else "\"result\""}:$result}"
    }

    fun constructJSONResponse(success: Boolean, result: String): String {
        return constructJSONResponse(success, "\"$result\"" as Any)
    }
}