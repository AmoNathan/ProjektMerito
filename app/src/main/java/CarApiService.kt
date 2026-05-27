package com.example.zguba.remote

import com.example.zguba.model.Car
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CarApiService {
    @GET("cars")
    suspend fun getCars(): List<Car>

    @POST("cars")
    suspend fun addCar(@Body car: Car): Car
}