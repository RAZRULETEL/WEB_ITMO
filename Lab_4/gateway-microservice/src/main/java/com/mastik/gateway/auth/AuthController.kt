package com.mastik.gateway.auth


import com.mastik.gateway.auth.jwt.JWTUtils
import com.mastik.gateway.auth.service.CrossServiceUserRepository
import io.jsonwebtoken.Jwts.header
import org.bouncycastle.cms.RecipientId.password
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter.Response
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.createEmptyContext
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
internal class AuthController {
    @Autowired
    private val userRepository: CrossServiceUserRepository? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    var jwtUtils: JWTUtils? = null

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun register(
        @RequestBody user: UserEntity
    ): Mono<ResponseEntity<Any>> {
        if (user.login == null || user.password == null) {
            return Mono.just(
                ResponseEntity.badRequest()
                    .body("{\"success\": 0, \"error\": \"login or password is null\"}")
            )
        }
        println("Registering user ${user.login}")
        val response =
            userRepository!!.registerUser(user.login!!, passwordEncoder!!.encode(user.password))
        println("Received user $response")
        return Mono.just(
            ResponseEntity.ok()
                .body("{\"success\": ${2 - response.status}, \"error\": \"${response.result as String}\"}")
        )
    }

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    fun login(
        @RequestBody user: UserEntity
    ): Mono<ResponseEntity<Any>> {
        var auth: Authentication? =
            UsernamePasswordAuthenticationToken(user.login, passwordEncoder!!.encode(user.password))
        var err: String = "user not exists"
        val response = ResponseEntity.ok()
        try {
            auth = authenticationManager!!.authenticate(auth)
            auth?.let {
                val userDetails: UserDetails = it.principal as UserDetails
                val jwtCookie: ResponseCookie = jwtUtils!!.generateJwtCookie(userDetails)
                response.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                err = ""
            }
        } catch (e: BadCredentialsException) {
            err = "invalid password"
        }
        return Mono.just(
            response
                .body(
                    "{\"success\": ${if (auth == null) 0 else 1}${
                        if (err.isEmpty())
                        ", \"result\": {\"user\": \"${user.login}\", \"expires\": ${System.currentTimeMillis() + jwtUtils!!.jwtExpirationMs}}"
                    else
                        ", \"error\": \"${err}\""
                    }}"
                )
        )
    }

    @RequestMapping("/logout")
    fun logout(): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity
                .ok()
                .header(
                    HttpHeaders.SET_COOKIE,
                    jwtUtils!!.cleanJwtCookie.toString()
                )
                .build()
        )
    }

    @GetMapping("/login")
    @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
    fun login() {
    }

    @GetMapping("/register")
    @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
    fun register() {
    }
}
