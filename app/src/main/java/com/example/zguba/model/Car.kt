package com.example.zguba.model

data class Car(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val price: Int,
    val imageUrl: String,
    val description: String = ""
)

