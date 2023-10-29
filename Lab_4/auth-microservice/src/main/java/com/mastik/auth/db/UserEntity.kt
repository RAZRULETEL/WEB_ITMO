package com.mastik.auth.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
class UserEntity(login: String, password: String) {

    @Id
    var login: String? = login

    var password: String? = password

    constructor() : this("", "")

    override fun toString(): String {
        return "UserEntity(login='$login', password='$password')"
    }
}