package servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


@WebServlet(name = "ControllerServlet", urlPatterns = [""])
class ControllerServlet() : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        if (req.getAttribute("check_error") == null)
            for (entry in req.parameterMap.entries) {
                if ("xyr".contains(entry.key)) {
                    req.setAttribute("timestamp", System.currentTimeMillis())
                    req.setAttribute("nanotime", System.nanoTime())
                    req.getRequestDispatcher("/CheckServlet").forward(req, resp)
                    return
                }
            }
        req.getRequestDispatcher("/place_dot.jsp").forward(req, resp)
    }
}