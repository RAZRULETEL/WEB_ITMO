package com.mastik.points.service

import com.mastik.gateway.communications.AuthRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.util.Random
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.SynchronousQueue

@Service
class PermanentServiceCommunicator() {
    companion object{
        const val RESPONSE_TIMEOUT = 5_000
    }

    @Value("\${mastik.corp.services.auth}")
    private val eurekaServiceId: String? = null

    @Autowired
    private var clients: DiscoveryClient? = null

    private val requests: SynchronousQueue<AuthRequest> = SynchronousQueue()
    private val results: ConcurrentHashMap<String, AuthRequest> = ConcurrentHashMap()

    private val singlePool = Executors.newSingleThreadExecutor()

    init{
        Thread{
            while (true){
                println("Searching for authentication service")
                var authServiceSocket: Socket? = null
                while (authServiceSocket == null || authServiceSocket.isClosed || !authServiceSocket.isConnected){
                    Thread.sleep(1_000)
                    val address = detectAuthenticationService()
                    println("Found authentication service at $address")
                    try {
                        address?.let {
                            authServiceSocket = Socket(address.address, 1234)
                        }
                    } catch (_: Exception){}
                }
                try {
                    val objectOutput  = ObjectOutputStream(authServiceSocket!!.getOutputStream())
                    val objectInput = ObjectInputStream(authServiceSocket!!.getInputStream())

                    singlePool.execute {
                        try {
                            while (!authServiceSocket!!.isClosed)
                                try {
                                    val response = objectInput.readObject() as AuthRequest
                                    results.put(response.id, response)
                                    println("Received response $response")
                                } catch (e: IOException) {
                                    if (e is SocketException) throw e
                                    e.printStackTrace()
                                }
                        } catch (e: Exception){
                            e.printStackTrace()
                        }
                    }

                    while (!authServiceSocket!!.isClosed) {
                        val request = requests.take()
                        println("Sending request $request")
                        objectOutput.writeObject(request)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun detectAuthenticationService(): InetSocketAddress? {
        if(clients == null)
            return null
        val applications: List<String> = clients!!.services

        if(applications.contains(eurekaServiceId)){
            val instances = clients!!.getInstances(eurekaServiceId)
            val instance = instances[Random().nextInt(instances.size)]

            return InetSocketAddress(instance.host, instance.port)
        }
        return null
    }

    fun executeRequest(request: AuthRequest): AuthRequest {
        requests.put(request)
        val start = System.currentTimeMillis()
        while(results[request.id] == null && System.currentTimeMillis() - start < RESPONSE_TIMEOUT);
        val res = results.getOrDefault(request.id, getServerErrorResult(request))
        results.remove(request.id)
        return res
    }

    fun getServerErrorResult(request: AuthRequest): AuthRequest{
        request.setResult(false, "Service not available" as Any)
        return request
    }
}