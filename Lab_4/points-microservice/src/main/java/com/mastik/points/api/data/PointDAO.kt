package com.mastik.points.api.data

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.Repository

@org.springframework.stereotype.Repository
interface PointDAO: CrudRepository<PointCheckResult, Int> {

    @Query("SELECT * FROM points")
    fun getAll(): List<PointCheckResult>

    @Modifying
    @Query("DELETE FROM points")
    fun clearAll(): Int
}