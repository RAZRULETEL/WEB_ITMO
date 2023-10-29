package com.mastik.points.service

import com.mastik.gateway.communications.AuthRequest
import com.mastik.gateway.communications.RequestType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class CrossServiceUserRepository() {

    @Autowired
    private val communicator: PermanentServiceCommunicator? = null

    fun getUserDetailsService(): UserDetailsService {

        return object : UserDetailsService {
            @Throws(UsernameNotFoundException::class)
            override fun loadUserByUsername(username: String): UserDetails {
                println("Getting user $username")
                val response = communicator!!.executeRequest(AuthRequest(RequestType.GET_USER, username))
                println("Received user $response")
                if(response.status == AuthRequest.SUCCESS)
                    return response.result as UserDetails
                else
                    throw UsernameNotFoundException(response.result as String)
            }
        }
    }

    fun registerUser(login: String, password: String): String{
        return communicator!!.executeRequest(AuthRequest(RequestType.REGISTER, User.withUsername(login).password(password).build())).result as String
    }
}
