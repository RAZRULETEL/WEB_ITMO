package com.mastik.gateway.auth


import com.mastik.gateway.DynamicGatewayFilter
import com.mastik.gateway.auth.jwt.JWTAuthFilter
import com.mastik.gateway.auth.service.CrossServiceUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.UriSpec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.server.SecurityWebFilterChain
import java.util.function.Function


//@EnableWebSecurity
@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Autowired
    var userRepository: CrossServiceUserRepository? = null

    @Autowired
    var jwtFilter: JWTAuthFilter? = null

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain? {
        return http
            .csrf { csrf ->
                csrf
                    .disable()
            }
            .authorizeExchange { auth ->
                auth
                    .pathMatchers("/register")
                    .permitAll()
                    .pathMatchers("/login")
                    .permitAll()
                    .pathMatchers("/logout")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            .formLogin { form ->
                form
                    .disable()
            }
            .logout { logout ->
                logout
                    .disable()
            }
            .httpBasic { httpBasic ->
                httpBasic
                    .disable()
            }
            .addFilterAt(jwtFilter!!, SecurityWebFiltersOrder.LOGOUT)
            .build()
    }

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator? {
        println("Build Locator")
        return builder.routes()
            .route(
                "path_route"
            ) { r: PredicateSpec ->
                r.path("/**")
                    .uri("no://op")
            }
            .build()
    }


    @Bean
    fun daoAuthenticationProvider(): AuthenticationProvider? {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsPasswordService(
            this.userRepository!!.getUserDetailsPasswordService()
        )
        provider.setUserDetailsService(this.userRepository!!.getUserDetailsService())
        return provider
    }


    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService? {
        return this.userRepository!!.getUserDetailsService()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}