package com.example.zguba.data

import android.content.Context
import com.example.zguba.model.Car
import com.example.zguba.remote.CarApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CarRepository {


    private const val BASE_URL = "https://carbackend-p51c.onrender.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(CarApiService::class.java)

    suspend fun getCars(context: Context): List<Car> {
        return try {
            api.getCars()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // W razie błędu zwracamy pustą listę
        }
    }

    suspend fun addCar(context: Context, car: Car) {
        try {
            api.addCar(car)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}