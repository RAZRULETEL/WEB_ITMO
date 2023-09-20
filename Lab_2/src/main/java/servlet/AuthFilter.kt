package servlet

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import java.io.IOException

@WebFilter("/")
class AuthFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
        request: ServletRequest, response: ServletResponse?,
        chain: FilterChain
    ) {
        var log = ""
        var pass = ""
        val session = (request as HttpServletRequest).getSession(false)
        if (session?.getAttribute("login") == null) {
            if (request.parameterMap["login"] == null && request.parameterMap["signin"] == null) request.getRequestDispatcher(
                "login.html"
            ).forward(request, response) else {
                if (request.parameterMap["login"] != null) {
                    if (DBHandler.instance!!.getUserAuth(
                            request.parameterMap["login"]?.get(0) ?: "",
                            request.parameterMap["pass"]?.get(0) ?: ""
                        )
                    ) {
                        log = request.parameterMap["login"]!![0]
                        pass = request.parameterMap["pass"]!![0]
                    } else {
                        request.getRequestDispatcher("login.html").forward(request, response)
                    }
                }
                if (request.parameterMap["signin"] != null) {
                    if (DBHandler.instance!!.registerUser(
                            request.parameterMap["signin"]?.get(0) ?: "",
                            request.parameterMap["pass"]?.get(0) ?: ""
                        )
                    ) {
                        log = request.parameterMap["signin"]!![0]
                        pass = request.parameterMap["pass"]!![0]
                    } else {
                        request.getRequestDispatcher("login.html").forward(request, response)
                    }
                }
            }
        }
        chain.doFilter(request, response)
        val sess = (request as HttpServletRequest).getSession(false)
        if(sess != null && log.isNotEmpty()) {
            sess.setAttribute("login", log)
            sess.setAttribute("pass", pass)
        }
    }
}