package com.example.zguba.data

import com.example.zguba.model.Car
import com.example.zguba.model.User
import com.example.zguba.remote.CarApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CarRepository {

    var currentUser: User? = null

    private const val BASE_URL = "https://carbackend-p51c.onrender.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(CarApiService::class.java)

    suspend fun getCars(): List<Car> {
        return try {
            api.getCars()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addCar(car: Car) {
        try {
            api.addCar(car)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun login(username: String): Boolean {
        return try {
            val user = api.login(mapOf("username" to username))
            currentUser = user
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Błąd połączenia lub brak użytkownika - nie pozwalamy na logowanie "na sucho"
            currentUser = null
            false
        }
    }
}