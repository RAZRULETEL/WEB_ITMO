package com.mastik.gateway.auth

import com.mastik.gateway.auth.service.CrossServiceUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.jvm.Throws


@Component
class RemoteAuthenticationManager : AuthenticationManager {

    @Autowired
    var userRepository: CrossServiceUserRepository? = null

    @Throws(BadCredentialsException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        if(authentication.principal == null) return null
        val user = User.withUsername(authentication.principal as String).password(authentication.credentials as String).build()
        try {
            if (userRepository!!.getUserDetailsService()
                    .loadUserByUsername(user.username) == user
            ) {
                val auth = UsernamePasswordAuthenticationToken(user, authentication.credentials as String, listOf(
                    GrantedAuthority { "USER" }))
                val context: SecurityContext = SecurityContextHolder.getContext()
                context.authentication = auth
                return auth
            } else {
                throw BadCredentialsException("bad credentials")
            }
        } catch (e: UsernameNotFoundException){
            return null
        }
    }
}