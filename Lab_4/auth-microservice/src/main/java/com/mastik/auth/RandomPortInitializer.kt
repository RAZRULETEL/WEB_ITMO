package com.mastik.points

import com.mastik.auth.SocketUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Configuration


@Configuration
class RandomPortInitializer :
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Value("\${port.number.min:8000}")
    private val minPort: Int = 0

    @Value("\${port.number.max:10000}")
    private val maxPort: Int = 0
    override fun customize(factory: ConfigurableServletWebServerFactory) {
        val port: Int = SocketUtils.findAvailableTcpPort(minPort, maxPort)
        factory.setPort(port)
        System.getProperties()["server.port"] = port
    }
}