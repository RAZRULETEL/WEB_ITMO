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

    fun renderTable(): String{
        val res = StringBuilder("<table id=\"dots-table\"><thead>")

        res.append("                <tr>\n" +
                "                    <th>x</th>\n" +
                "                    <th>y</th>\n" +
                "                    <th>r</th>\n" +
                "                    <th style=\"padding: 0;\">\n" +
                "                        <label class=\"custom-select\">\n" +
                "                            <select id=\"table-time-format\" onchange=\"updateTimeColumn()\">\n" +
                "                                <option value=\"0\">timestamp, ms</option>\n" +
                "                                <option value=\"1\">Дата и время</option>\n" +
                "                                <option value=\"2\">Дата и время (кратко)</option>\n" +
                "                                <option value=\"3\">Точное время</option>\n" +
                "                            </select>\n" +
                "                            <svg>\n" +
                "                                <use xlink:href=\"#select-arrow-down\"></use>\n" +
                "                            </svg>\n" +
                "                        </label></th>\n" +
                "                    <th>Время выполнения, ms</th>\n" +
                "                </tr>").append("</thead><tbody>")

        for(point in results){
            res.append("<tr style='color: ${if(point.success) "#00c905" else "red"};'><td>${point.x}</td><td>${point.y}</td><td>${point.r}</td><td>${point.timestamp}</td><td>${point.executionTime}</td></tr>")
        }

        res.append("                        <tr id=\"points-table-filler\">\n" +
                "                            <td style=\"height: auto;\" colspan=\"5\"></td>\n" +
                "                        </tr>")

        return res.append("</tbody></table>").toString()
    }

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
        return results.size.toString()
    }
}