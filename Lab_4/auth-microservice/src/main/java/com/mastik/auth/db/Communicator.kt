package com.mastik.auth.db

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class Communicator(private val sock: Socket) {
    private val outStream = ObjectOutputStream(sock.getOutputStream())
    private val inStream = ObjectInputStream(sock.getInputStream())
    private var isProcessing = AtomicBoolean(false)

    fun readObject(): Any{
        isProcessing.set(true)
        val obj = inStream.readObject()
        isProcessing.set(false)
        return obj
    }

    fun writeObject(obj: Any) {
        outStream.writeObject(obj)
    }
}