package com.mastik.points

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import java.sql.DriverManager
import java.util.Timer
import java.util.TimerTask

@SpringBootApplication
@EnableDiscoveryClient
class Main{

    @Value("\${spring.datasource.url}")
    private val dbHost: String? = null

    @Value("\${spring.datasource.username}")
    private val dbUser: String? = null

    @Value("\${spring.datasource.password}")
    private val dbPass: String? = null

    init {
        val dbInitTimer = Timer()
        dbInitTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(dbHost == null) return
                dbInitTimer.cancel()
                DriverManager.getConnection(
                    dbHost,
                    dbUser,
                    dbPass
                ).createStatement().executeUpdate("""
                        CREATE TABLE IF NOT EXISTS "points" (
                            "id" INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
                            "x" double precision NOT NULL,
                            "y" double precision NOT NULL,
                            "r" double precision NOT NULL,
                            "timestamp" bigint NOT NULL,
                            "execution_time" double precision NOT NULL,
                            "success" boolean NOT NULL
                        );
                    """.trimIndent())
            }
        }, 100, 100)
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}