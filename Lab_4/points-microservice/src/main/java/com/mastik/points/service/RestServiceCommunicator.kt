package com.mastik.points.service

import com.mastik.gateway.communications.AuthRequest
import com.mastik.gateway.communications.RequestType
import com.mastik.gateway.communications.UserDetailsEntity
import org.apache.http.HttpEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Service
class RestServiceCommunicator() {

    @Value("\${mastik.corp.services.auth}")
    private val eurekaServiceId: String? = null

    @Autowired
    var restTemplate: RestTemplate? = null

    fun executeRequest(request: AuthRequest): AuthRequest {
        try {
            if(request.type == RequestType.GET_USER){
                val entity = restTemplate!!.getForObject(
                    "http://$eurekaServiceId/get?login=${request.payload}",
                    UserDetailsEntity::class.java
                )
                request.setResult(entity != null, entity?.toUserDetails() ?: "User not found" as Any)
            }
            if(request.type == RequestType.REGISTER){
                val entity = restTemplate!!.exchange(
                    "http://$eurekaServiceId/register",
                    HttpMethod.POST,
                    org.springframework.http.HttpEntity<UserDetails>(request.payload as UserDetails),
                    UserDetailsEntity::class.java
                ).body
                request.setResult(entity != null, entity?.toUserDetails() ?: "User already exists" as Any)
            }
            if(request.type == RequestType.CHANGE_PASSWORD){
                val entity = restTemplate!!.exchange(
                    "http://$eurekaServiceId/change_password",
                    HttpMethod.POST,
                    org.springframework.http.HttpEntity<UserDetails>(request.payload as UserDetails),
                    UserDetailsEntity::class.java
                ).body
                request.setResult(entity != null, entity?.toUserDetails() ?: "Unknown error" as Any)
            }
            return request
        }catch (e: Exception){
            return getServerErrorResult(request)
        }
    }

    fun getServerErrorResult(request: AuthRequest): AuthRequest{
        request.setResult(false, "Service not available" as Any)
        return request
    }
}