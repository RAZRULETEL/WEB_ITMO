package com.mastik.gateway.auth.jwt

import java.security.Key;
import java.util.Date;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.userdetails.UserDetails

@Component
class JWTUtils {

    companion object{
        private const val jwtCookie: String = "JWT"
    }

    @Value("\${mastik.corp.jwt-secret}")
    private val jwtSecret: String? = null

    @Value("\${mastik.corp.jwt-expires}")
    val jwtExpirationMs: Int = 0

    fun getJwtFromCookies(request: ServerHttpRequest): String? {
        return request.cookies[jwtCookie]?.first()?.value
    }

    fun generateJwtCookie(userPrincipal: UserDetails): ResponseCookie {
        val jwt = generateTokenFromUsername(userPrincipal.username)
        return ResponseCookie.from(jwtCookie, jwt).maxAge((jwtExpirationMs / 1000).toLong())
            .httpOnly(true).build()
    }

    val cleanJwtCookie: ResponseCookie
        get() = ResponseCookie.from(jwtCookie, null).build()

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(key()).build()
            .parseClaimsJws(token).body.subject
    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken)
            return true
        } catch (e: MalformedJwtException) {
            println("Invalid JWT token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            println("JWT token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            println("JWT token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("JWT claims string is empty: ${e.message}")
        }
        return false
    }

    fun generateTokenFromUsername(username: String?): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact()
    }
}