package beans

import db.DBHandler
import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Named
import types.Point
import types.PointCheckResult
import java.io.Serializable

@Named("results")
@SessionScoped
class PointResults: Serializable {
    private val results: MutableList<PointCheckResult> = DBHandler.instance.getAllResults()
    val point: Point = Point()
    val canvasPoint: Point = Point()

    fun add() {
        val nano = System.nanoTime();
        results.add(point.getResult(nano))
        DBHandler.instance.addResult(point.getResult(nano))
    }

    fun addFromCanvas(){
        val nano = System.nanoTime();
        results.add(canvasPoint.getResult(nano))
        DBHandler.instance.addResult(canvasPoint.getResult(nano))
    }

    fun clear() {
        results.clear()
        DBHandler.instance.clearResults()
    }

    fun getAll(): List<PointCheckResult>{
        return results
    }

    override fun toString(): String {
        return results.toString()
    }
}