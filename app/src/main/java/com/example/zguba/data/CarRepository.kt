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
            make = "BMW",
            model = "M4",
            year = 2023,
            price = 89990,
            imageUrl = "https://images.unsplash.com/photo-1617531653332-bd46c24f2068?w=800&auto=format&fit=crop",
            description = ""
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
            make = "Ferrari",
            model = "F8",
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
            imageUrl = "https://images.unsplash.com/photo-1610880846497-7257b23f6138?q=80&w=1921&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "Supercar with V10 engine"
        ),
        Car(
            id = "6",
            make = "Ferrari",
            model = "F8 Tributo",
            year = 2024,
            price = 276000,
            imageUrl = "https://images.unsplash.com/photo-1730110206438-84d983766aa1?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "Italian supercar masterpiece"
        ),
        Car(
            id = "7",
            make = "Lamborghini",
            model = "Huracán",
            year = 2023,
            price = 208571,
            imageUrl = "https://images.unsplash.com/photo-1621285853634-713b8dd6b5fd?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "Exotic supercar with aggressive design"
        ),
        Car(
            id = "8",
            make = "Ford",
            model = "Mustang GT",
            year = 2024,
            price = 42995,
            imageUrl = "https://images.unsplash.com/photo-1597274324473-c3ced481af9c?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "American muscle car classic"
        ),
        Car(
            id = "9",
            make = "Chevrolet",
            model = "Corvette",
            year = 2024,
            price = 65995,
            imageUrl = "https://images.unsplash.com/photo-1611367687299-3bc21f590b3d?q=80&w=698&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            description = "Mid-engine American sports car"
        ),
        Car(
            id = "10",
            make = "Nissan",
            model = "GT-R",
            year = 2023,
            price = 113540,
            imageUrl = "https://images.unsplash.com/photo-1611859266238-4b98091d9d9b?q=80&w=764&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
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
