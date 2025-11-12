package com.example.zguba.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String, // In production, this should be hashed
    val createdAt: Long = System.currentTimeMillis()
)

