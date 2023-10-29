package com.mastik.gateway.communications

import jakarta.security.auth.message.AuthStatus.SUCCESS
import java.io.Serializable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class AuthRequest(
    id: String,
    val type: RequestType,
    val payload: Any
) : Serializable {
    val id: String

    constructor(type: RequestType, payload: Any) : this(
        java.util.UUID.randomUUID().toString(),
        type,
        payload
    )

    init {
        this.id = id
    }


    companion object {
        const val CREATED = 0
        const val SUCCESS = 1
        const val ERROR = 2

        @JvmStatic
        private val serialVersionUID: Long = 2
    }

    var status: Int = CREATED
        private set
    var result: Any? = null
        private set

    fun setResult(
        success: Boolean,
        result: Any
    ) {
        if (this.status != CREATED)
            throw IllegalStateException("result already exists")
        this.status = if (success) SUCCESS else ERROR
        this.result = result
    }

    override fun toString(): String {
        return "AuthRequest(type=$type, payload=$payload, status=$status, id=$id, result=$result)"
    }
}