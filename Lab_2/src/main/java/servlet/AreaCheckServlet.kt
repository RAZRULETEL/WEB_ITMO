package servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import types.PointCheckResult
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong


@WebServlet(name = "CheckServlet", urlPatterns = ["/CheckServlet"])
class AreaCheckServlet() : HttpServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "text/html"
        resp.characterEncoding = StandardCharsets.UTF_8.name()
        val writer = resp.writer
        try {
            if (!req.parameterMap.containsKey("x") || !req.parameterMap.containsKey("y") || !req.parameterMap.containsKey("r")) {
                println("No values")
                forwardToController(req, resp)
                return
            }
            val x = req.parameterMap.getValue("x")[0].replace(",", ".").toFloat()
            val y = req.parameterMap.getValue("y")[0].replace(",", ".").toFloat()
            val r = req.parameterMap.getValue("r")[0].replace(",", ".").toFloat()

            val res = PointCheckResult(x, y, r, req.getAttribute("timestamp") as Long, req.getAttribute("nanotime") as Long)

            val session: HttpSession = req.session
            var pointsArr: ArrayList<PointCheckResult>? = session.getAttribute("points") as ArrayList<PointCheckResult>?
            if (pointsArr == null)
                pointsArr = ArrayList();
            pointsArr.add(res);
            session.setAttribute("points", pointsArr)

            if (req.getHeader("Accept") == null || !req.getHeader("Accept").contains("application/json")) {
                session.setAttribute("redirect", true);
                resp.setHeader("Content-type", "text/html")

                if (!validateX(x) || !validateY(y) || !validateR(r)) {
                    println("Validation error")
                    forwardToController(req, resp)
                    return
                }

                var html = servletContext.getResource("/dot_check.html")?.readText() ?: return
                html = html.replace(HEADER, if (res.success) "<h1 class=\"header success\">Congratulation !!!</h1>" else "<h1 class=\"header fail\">Bad try</h1>");


                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                val dateString = LocalDateTime.ofEpochSecond((res.timestamp / 1e3).roundToLong(), (res.timestamp % 1e3).toInt(), ZoneOffset.ofHours(3)).format(formatter)

                val data = """
    <h2>Ваши данные:</h2>
    <p>X: ${x}</p>
    <p>Y: ${y}</p>
    <p>R: ${r}</p>
    <h2>Результат:</h2>
    <p>Успех: ${if (res.success) "успешный" else "не успешный"}</p>
    <p>Время выполнения, ms: ${res.executionTime}</p>
    <p>Дата и время: ${dateString}</p>
    <input type="button" value="Вернуться" onclick="redirectToForm()">
            """.trimIndent()

                html = html.replace(INFO, data).replace(POINT, res.toString())

                writer.write(html)


            } else {
                resp.setHeader("Content-type", "application/json")
                writer.write(res.toString())
            }
        } catch (e: NumberFormatException) {
            if (req.getHeader("Accept") == null || !req.getHeader("Accept").contains("application/json")) {
                println(e.message)
                forwardToController(req, resp)
            } else {
                resp.setHeader("Content-type", "application/json")
                writer.write("{\"error\": ${e.message}}")
            }
        } finally {
            writer.close()
        }
    }

    private fun validateX(x: Float): Boolean {
        return x in MIN_X..MAX_X;
    }

    private fun validateY(y: Float): Boolean {
        return y in MIN_Y..MAX_Y;
    }

    private fun validateR(r: Float): Boolean {
        return r in MIN_R..MAX_R;
    }

    private fun forwardToController(req: HttpServletRequest, resp: HttpServletResponse) {
        req.setAttribute("check_error", "1");
        req.getRequestDispatcher("").forward(req, resp);
    }

    companion object {
        const val HEADER = "<%header%>"
        const val INFO = "<%info%>"
        const val POINT = "<%point%>"
        const val MIN_X = -2f
        const val MAX_X = 2f
        const val MIN_Y = -3.0f
        const val MAX_Y = 5f
        const val MIN_R = 1f
        const val MAX_R = 3f
    }
}