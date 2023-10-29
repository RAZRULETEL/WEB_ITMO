package com.mastik.points

import com.mastik.points.jwt.JWTAuthFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private val jwtFilter: JWTAuthFilter? = null

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf
                    .disable()
            }
            .authorizeHttpRequests {auth ->
            auth
                .anyRequest()
                .authenticated()
            }
            .addFilterAfter(jwtFilter, LogoutFilter::class.java)
        return http.build()
    }
}