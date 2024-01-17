package com.mastik.auth

import com.mastik.auth.db.UserDAO
import com.mastik.gateway.communications.UserDetailsEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @Autowired
    private val userRepository: UserDAO? = null

    @GetMapping("/get")
    fun getUser(@RequestParam("login") userName: String): UserDetails? {
        val user = userRepository!!.getUser(userName)
        if (user.isPresent)
            return User.withUsername(user.get().login)
                .password(user.get().password).build()
        else
            return null
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody entity: UserDetailsEntity): UserDetails? {
        val user = entity.toUserDetails() ?: return null
        try {
            userRepository!!.registerUser(user.username, user.password)
            return user
        } catch (e: DuplicateKeyException) {
            return null
        }
    }

    @PostMapping("/change-password")
    fun changePassword(@RequestBody entity: UserDetailsEntity): UserDetails? {
        val user = entity.toUserDetails() ?: return null
        try {
            userRepository!!.updateUserPassword(user.username, user.password)
            return user
        } catch (e: DuplicateKeyException) {
            return null
        }
    }
}