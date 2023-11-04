package com.mastik.gateway.auth.service

import com.mastik.gateway.communications.AuthRequest
import com.mastik.gateway.communications.RestServiceCommunicator
import com.mastik.gateway.communications.RequestType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CrossServiceUserRepository() {

    @Autowired
    private val communicator: RestServiceCommunicator? = null

    fun getUserDetailsService(): UserDetailsService {
        return UserDetailsService {username ->
            println("Getting user $username")
            val response = communicator!!.executeRequest(AuthRequest(RequestType.GET_USER, username)).share().block()
            println("Received user $response")
            if(response!!.status == AuthRequest.SUCCESS)
                return@UserDetailsService response.result as UserDetails
            else
                throw UsernameNotFoundException(response.result as String)
        }
    }

    fun getUserDetailsPasswordService(): UserDetailsPasswordService {// TODO: Add adequate implementation
        return UserDetailsPasswordService { userDetails: UserDetails, password: String  ->
            val newDetails = User.withUsername(userDetails.username).password(password).roles("USER").build()
            val res = communicator!!.executeRequest(AuthRequest(RequestType.CHANGE_PASSWORD, newDetails)).share().block()!!.result as UserDetails
            return@UserDetailsPasswordService res
        }
    }

    fun registerUser(login: String, password: String): AuthRequest{
        return communicator!!.executeRequest(AuthRequest(RequestType.REGISTER, User.withUsername(login).password(password).build())).share().block()!!
    }
}
