package com.mastik.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableDiscoveryClient
class Main  {
//    @Autowired
//    var filter: DynamicGatewayFilter? = null

//    @Bean
//    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator {
//        return builder.routes().route { p: PredicateSpec ->
//            p.path("/**").filters { f: GatewayFilterSpec ->
//                f.filter(
//                    filter
//                )
//            }.uri("no://op")
//        }.build()
//    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
