package com.mastik.points.jwt

import com.mastik.points.service.CrossServiceUserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthFilter: OncePerRequestFilter() {

    @Autowired
    private val jwtUtils: JWTUtils? = null

    @Autowired
    private val userRepository: CrossServiceUserRepository? = null

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            val jwt = parseJwt(request);
            if (jwt != null && jwtUtils!!.validateJwtToken(jwt)) {
                val username = jwtUtils.getUserNameFromJwtToken(jwt);

                val userDetails = userRepository!!.getUserDetailsService().loadUserByUsername(username);

                val authentication = UsernamePasswordAuthenticationToken(userDetails,
                    null,
                    userDetails.authorities
                );

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request);

                SecurityContextHolder.getContext().authentication = authentication;
            }
        } catch (e: Exception) {
            println("Cannot set user authentication: ${e}")
        }

        filterChain.doFilter(request, response);
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        return jwtUtils!!.getJwtFromCookies(request)
    }
}