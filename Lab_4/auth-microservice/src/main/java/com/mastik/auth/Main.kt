package com.mastik.auth

import com.mastik.auth.db.Communicator
import com.mastik.auth.db.UserDAO
import com.mastik.gateway.communications.AuthRequest
import com.mastik.gateway.communications.RequestType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import java.io.StreamCorruptedException
import java.net.ServerSocket
import java.net.SocketException
import java.sql.DriverManager
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors

@SpringBootApplication
@EnableDiscoveryClient
class Main {
    private val requestHandlerPool = Executors.newCachedThreadPool()

    @Autowired
    private val userRepository: UserDAO? = null

    @Value("\${spring.datasource.url}")
    private val dbHost: String? = null

    @Value("\${spring.datasource.username}")
    private val dbUser: String? = null

    @Value("\${spring.datasource.password}")
    private val dbPass: String? = null

    init {
        Thread {
            val dbInitTimer = Timer()
            dbInitTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (dbHost == null) return
                    dbInitTimer.cancel()
                    DriverManager.getConnection(
                        dbHost,
                        dbUser,
                        dbPass
                    ).createStatement().executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS "users" (
                            "login" text PRIMARY KEY NOT NULL,
                            "password" text NOT NULL
                        );
                    """.trimIndent()
                    )
                }
            }, 100, 100)

            val server = ServerSocket(1234)

            while (!server.isClosed) {
                val client = server.accept()
                println("Received client $client")
                val communicator = Communicator(client)
                requestHandlerPool.execute {
                    try {
                        while (true)
                            try {
                                val request = communicator.readObject() as AuthRequest
                                println("Received $request")
                                if (request.type == RequestType.GET_USER) {
                                    val user = userRepository!!.getUser(request.payload as String)
                                    if (user.isPresent)
                                        request.setResult(
                                            true,
                                            User.withUsername(user.get().login)
                                                .password(user.get().password).build()
                                        )
                                    else
                                        request.setResult(false, "User not exists")
                                }
                                if (request.type == RequestType.REGISTER) {
                                    val user = request.payload as UserDetails
                                    try {
                                        userRepository!!.registerUser(user.username, user.password)
                                        request.setResult(true, "successfully registered")
                                    } catch (e: DuplicateKeyException) {
                                        request.setResult(false, "user already exists")
                                    }
                                }
                                if (request.type == RequestType.CHANGE_PASSWORD) {
                                    val newUser = request.payload as UserDetails
                                    try {
                                        userRepository!!.updateUserPassword(
                                            newUser.username,
                                            newUser.password
                                        )
                                        request.setResult(true, "successfully updated")
                                    } catch (e: Exception) {
                                        request.setResult(false, "user doesn't exists")
                                    }
                                }
                                communicator.writeObject(request)
                            } catch (_: IllegalStateException) {
                            } catch (e: StreamCorruptedException) {
                                println(e.message)
                            }
                    } catch (e: SocketException) {
                        println("Socket: " + e.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }.start()
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
