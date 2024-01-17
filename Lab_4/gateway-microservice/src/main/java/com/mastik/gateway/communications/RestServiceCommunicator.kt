package com.mastik.gateway.communications

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class RestServiceCommunicator() {

    @Value("\${mastik.corp.services.auth}")
    private val eurekaServiceId: String? = null

    @Autowired
    var restTemplate: RestTemplate? = null

    fun executeRequest(request: AuthRequest): Mono<AuthRequest> {
        try {
            var blockingWrapper: Mono<AuthRequest>? = null
            if (request.type == RequestType.GET_USER) {
                blockingWrapper = Mono.fromCallable<AuthRequest>(fun(): AuthRequest {
                    try {
                        val entity = restTemplate!!.getForObject(
                            "http://$eurekaServiceId/get?login=${request.payload}",
                            UserDetailsEntity::class.java
                        )
                        request.setResult(
                            entity != null,
                            entity?.toUserDetails() ?: "User not found" as Any
                        )
                    } catch (e: Exception) {
                        println(e.message)
                        return getServerErrorResult(request)
                    }
                    return request
                })
            }
            if (request.type == RequestType.REGISTER) {
                blockingWrapper = Mono.fromCallable<AuthRequest>(fun(): AuthRequest {
                    try {
                        val entity = restTemplate!!.exchange(
                            "http://$eurekaServiceId/register",
                            HttpMethod.POST,
                            org.springframework.http.HttpEntity<UserDetails>(request.payload as UserDetails),
                            UserDetailsEntity::class.java
                        ).body
                        request.setResult(
                            entity != null,
                            entity?.toUserDetails() ?: "User already exists" as Any
                        )
                    } catch (e: Exception) {
                        println(e.message)
                        return getServerErrorResult(request)
                    }
                    return request
                })
            }
            if (request.type == RequestType.CHANGE_PASSWORD) {
                blockingWrapper = Mono.fromCallable<AuthRequest>(fun(): AuthRequest {
                    try {
                        val entity = restTemplate!!.exchange(
                            "http://$eurekaServiceId/change_password",
                            HttpMethod.POST,
                            org.springframework.http.HttpEntity<UserDetails>(request.payload as UserDetails),
                            UserDetailsEntity::class.java
                        ).body
                        request.setResult(
                            entity != null,
                            entity?.toUserDetails() ?: "Unknown error" as Any
                        )
                    } catch (e: Exception) {
                        println(e.message)
                        return getServerErrorResult(request)
                    }
                    return request
                })
            }
            blockingWrapper =
                blockingWrapper?.subscribeOn(Schedulers.boundedElastic()) ?: Mono.just(
                    getServerErrorResult(request)
                )
            return blockingWrapper!!
        } catch (e: Exception) {
            println(e.message)
            return Mono.just(getServerErrorResult(request))
        }
    }

    fun getServerErrorResult(request: AuthRequest): AuthRequest {
        request.setResult(false, "Service not available" as Any)
        return request
    }
}