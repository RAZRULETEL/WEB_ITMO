package com.mastik.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer


@SpringBootApplication
@EnableEurekaServer
class MyClass

fun main(args: Array<String>) {
    runApplication<MyClass>(*args)
}

