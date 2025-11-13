package com.example.zguba.data

import android.content.Context
import com.example.zguba.data.local.CarDatabase
import com.example.zguba.data.local.toDomain
import com.example.zguba.data.local.toEntity
import com.example.zguba.model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CarRepository {

    private val seedCars = listOf(
        Car(
            id = "1",
            make = "Tesla",
            model = "Model S",
            year = 2023,
            price = 89990,
            imageUrl = "https://images.unsplash.com/photo-1617531653332-bd46c24f2068?w=800&auto=format&fit=crop",
            description = "Electric luxury sedan with autopilot"
        ),
        Car(
            id = "2",
            make = "BMW",
            model = "M3",
            year = 2024,
            price = 74900,
            imageUrl = "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&auto=format&fit=crop",
            description = "High-performance sports sedan"
        ),
        Car(
            id = "3",
            make = "Mercedes-Benz",
            model = "AMG GT",
            year = 2023,
            price = 129900,
            imageUrl = "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&auto=format&fit=crop",
            description = "Luxury sports car with V8 engine"
        ),
        Car(
            id = "4",
            make = "Porsche",
            model = "911",
            year = 2024,
            price = 114400,
            imageUrl = "https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?w=800&auto=format&fit=crop",
            description = "Iconic sports car, legendary performance"
        ),
        Car(
            id = "5",
            make = "Audi",
            model = "R8",
            year = 2023,
            price = 169900,
            imageUrl = "https://images.unsplash.com/photo-1606664515524-ed2f786a0ad6?w=800&auto=format&fit=crop",
            description = "Supercar with V10 engine"
        ),
        Car(
            id = "6",
            make = "Ferrari",
            model = "F8 Tributo",
            year = 2024,
            price = 276000,
            imageUrl = "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=800&auto=format&fit=crop",
            description = "Italian supercar masterpiece"
        ),
        Car(
            id = "7",
            make = "Lamborghini",
            model = "Huracán",
            year = 2023,
            price = 208571,
            imageUrl = "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=800&auto=format&fit=crop",
            description = "Exotic supercar with aggressive design"
        ),
        Car(
            id = "8",
            make = "Ford",
            model = "Mustang GT",
            year = 2024,
            price = 42995,
            imageUrl = "https://images.unsplash.com/photo-1606664515524-ed2f786a0ad6?w=800&auto=format&fit=crop",
            description = "American muscle car classic"
        ),
        Car(
            id = "9",
            make = "Chevrolet",
            model = "Corvette",
            year = 2024,
            price = 65995,
            imageUrl = "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=800&auto=format&fit=crop",
            description = "Mid-engine American sports car"
        ),
        Car(
            id = "10",
            make = "Nissan",
            model = "GT-R",
            year = 2023,
            price = 113540,
            imageUrl = "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=800&auto=format&fit=crop",
            description = "Godzilla - Japanese supercar"
        )
    )

    private fun dao(context: Context) = CarDatabase.getInstance(context).carDao()

    suspend fun getCars(context: Context): List<Car> = withContext(Dispatchers.IO) {
        val carDao = dao(context)
        val stored = carDao.getCars()
        if (stored.isNotEmpty()) {
            return@withContext stored.map { it.toDomain() }
        }

        carDao.upsertCars(seedCars.map { it.toEntity() })
        seedCars
    }

    suspend fun addCar(context: Context, car: Car) = withContext(Dispatchers.IO) {
        val carDao = dao(context)
        carDao.upsertCar(car.toEntity())
    }
}
