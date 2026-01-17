package com.example.zguba.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zguba.model.Car

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val price: Int,
    val imageUrl: String,
    val description: String
)

fun CarEntity.toDomain(): Car =
    Car(
        id = id,
        make = make,
        model = model,
        year = year,
        price = price,
        imageUrl = imageUrl,
        description = description
    )

fun Car.toEntity(): CarEntity =
    CarEntity(
        id = id,
        make = make,
        model = model,
        year = year,
        price = price,
        imageUrl = imageUrl,
        description = description
    )

