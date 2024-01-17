package com.mastik.gateway.communications

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsEntity {
    val password: String? = null
    val username: String? = null
    val authorities: Array<String>? = null
    val accountNonExpired: Boolean? = null
    val accountNonLocked: Boolean? = null
    val credentialsNonExpired: Boolean? = null
    val enabled: Boolean? = null

    fun toUserDetails(): UserDetails {
        return User.withUsername(username).password(password).authorities(*authorities?: arrayOf("USER")).build()
    }
}
