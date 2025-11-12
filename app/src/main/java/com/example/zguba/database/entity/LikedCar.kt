package com.example.zguba.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "liked_cars",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LikedCar(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val carId: String,
    val carMake: String,
    val carModel: String,
    val carYear: Int,
    val carPrice: Int,
    val carImageUrl: String,
    val carDescription: String,
    val likedAt: Long = System.currentTimeMillis()
)

