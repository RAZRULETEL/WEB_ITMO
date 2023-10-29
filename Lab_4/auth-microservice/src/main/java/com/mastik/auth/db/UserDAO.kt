package com.mastik.auth.db

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.util.Optional

@org.springframework.stereotype.Repository
interface UserDAO: Repository<UserEntity, String> {

    @Query("SELECT * FROM users WHERE login = :login")
    fun getUser(@Param("login") login: String): Optional<UserEntity>

    @Query("SELECT login FROM users WHERE login = :login AND password = :password")
    fun checkUserAuth(@Param("login") login: String, @Param("password") password: String): List<UserEntity>

    @Modifying
    @Query("INSERT INTO users(login, password) VALUES(:login, :password)")
    fun registerUser(@Param("login") login: String, @Param("password") password: String)

    @Modifying
    @Query("UPDATE users SET password = :password WHERE login = :login")
    fun updateUserPassword(@Param("login") login: String, @Param("password") password: String)

    @Modifying
    @Query("DELETE FROM users")
    fun clearUsers()

}