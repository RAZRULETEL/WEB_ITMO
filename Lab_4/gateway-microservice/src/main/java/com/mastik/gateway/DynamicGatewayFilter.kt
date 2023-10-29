package com.mastik.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import java.util.function.Consumer

@Component
class DynamicGatewayFilter: GlobalFilter {

    @Value("\${mastik.corp.services.points}")
    private val POINTS_SERVICE: String? = null

    @Autowired
    var clients: DiscoveryClient? = null

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        println("FILTER!")
        val applications = clients!!.services
        if(exchange.request.path.value().startsWith("/api"))
            if(applications.contains(POINTS_SERVICE)){
                val instances = clients!!.getInstances(POINTS_SERVICE)
                val instance = instances[Random().nextInt(instances.size)]
                val uri = URI("http://${instance.host}:${instance.port}/${exchange.request.path.value().split("/").last()}")
                println("Redirecting user to: $uri")
                exchange.attributes[ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR] = uri
            }


//        applications.forEach(Consumer { serviceId: String ->
//            println(serviceId)
//            val serviceApiSet = ServiceMapper[serviceId]
//            if(serviceApiSet!!.contains(exchange.request.path.value().split("/").last().split("?").first()))
//            {
//                val instance = clients!!.getInstances(serviceId)[0]
//                exchange.attributes[ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR] = URI("http://${instance.host}:${instance.port}/${exchange.request.path.value().split("/").last()}")
//            }
//        })

        return chain.filter(exchange)
    }
}
