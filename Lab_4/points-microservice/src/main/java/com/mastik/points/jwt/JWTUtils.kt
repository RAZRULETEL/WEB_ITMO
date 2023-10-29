package com.mastik.points.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils
import java.security.Key
import java.security.SignatureException

@Component
class JWTUtils {

    companion object{
        private const val jwtCookie: String = "JWT"
    }

    @Value("\${mastik.corp.jwt-secret}")
    private val jwtSecret: String? = null

    fun getJwtFromCookies(request: HttpServletRequest?): String? {
        return request?.let { WebUtils.getCookie(it, jwtCookie) }?.value
    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(key()).build()
            .parseClaimsJws(token).body.subject
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
        } catch (e: io.jsonwebtoken.security.SignatureException) {
            println("JWT signature not match: ${e.message}")
        }
        return false
    }
}