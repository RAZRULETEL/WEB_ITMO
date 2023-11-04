package com.mastik.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced

import org.springframework.context.annotation.Bean


@SpringBootApplication
@EnableDiscoveryClient
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
