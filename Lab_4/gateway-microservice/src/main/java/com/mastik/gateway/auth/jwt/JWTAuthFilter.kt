package com.mastik.gateway.auth.jwt

import com.mastik.gateway.auth.service.CrossServiceUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JWTAuthFilter : WebFilter {

    @Autowired
    private val jwtUtils: JWTUtils? = null

    @Autowired
    var userRepository: CrossServiceUserRepository? = null

    override fun filter(
        exchange: ServerWebExchange,
        filterChain: WebFilterChain
    ): Mono<Void> {
        try {
            val jwt = parseJwt(exchange.request);
            if (jwt != null && jwtUtils!!.validateJwtToken(jwt)) {
                val username = jwtUtils.getUserNameFromJwtToken(jwt)

                val userDetails =
                    userRepository!!.getUserDetailsService().loadUserByUsername(username)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    listOf(GrantedAuthority { "USER" })
                )
                SecurityContextHolder.getContext().authentication = authentication
                return filterChain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
            }
        } catch (e: Exception) {
            println("Cannot set user authentication: ${e}");
        }


        return filterChain.filter(exchange)
    }

    private fun parseJwt(request: ServerHttpRequest): String? {
        return jwtUtils!!.getJwtFromCookies(request)
    }
}