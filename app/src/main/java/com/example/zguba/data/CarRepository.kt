package com.example.zguba.data

import android.content.Context
import com.example.zguba.data.local.CarDatabase
import com.example.zguba.data.local.toDomain
import com.example.zguba.data.local.toEntity
import com.example.zguba.model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CarRepository: Data access layer for car information
 * 
 * Purpose:
 * - Abstracts database operations from UI components
 * - Provides simple API for getting and adding cars
 * - Handles database seeding (initial data population)
 * - Converts between domain models (Car) and database entities (CarEntity)
 * 
 * Pattern: Repository Pattern
 * - UI components don't know about database implementation
 * - All database access goes through this repository
 * - Easy to change storage mechanism without affecting UI
 */
object CarRepository {

    /**
     * Seed Cars: Default cars to populate database on first launch
     * 
     * These 10 example cars are inserted into the database when:
     * - App is launched for the first time
     * - Database is empty
     * 
     * After seeding, users can add their own cars which are stored alongside these
     */
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

    /**
     * Helper Function: Get database DAO (Data Access Object)
     * 
     * Steps:
     * 1. Get singleton database instance
     * 2. Return the CarDao interface for database operations
     * 
     * @param context: Android context needed to access database
     * @return CarDao: Interface for car database operations
     */
    private fun dao(context: Context) = CarDatabase.getInstance(context).carDao()

    /**
     * Get Cars: Retrieve all cars from database
     * 
     * Flow:
     * 1. Run on background thread (Dispatchers.IO) to avoid blocking UI
     * 2. Get DAO from database
     * 3. Query database for all stored cars
     * 4. If database has cars:
     *    - Convert CarEntity objects to Car domain objects
     *    - Return the list
     * 5. If database is empty:
     *    - Convert seed cars to CarEntity objects
     *    - Insert them into database (upsert = insert or update)
     *    - Return the seed cars list
     * 
     * @param context: Android context for database access
     * @return List<Car>: All cars from database (or seed cars if empty)
     */
    suspend fun getCars(context: Context): List<Car> = withContext(Dispatchers.IO) {
        // Get database access object
        val carDao = dao(context)
        
        // Query database for all cars
        val stored = carDao.getCars()
        
        // If database has cars, return them (converted to domain objects)
        if (stored.isNotEmpty()) {
            return@withContext stored.map { it.toDomain() }
        }

        // Database is empty: Seed with default cars
        // Convert Car objects to CarEntity objects and insert into database
        carDao.upsertCars(seedCars.map { it.toEntity() })
        
        // Return the seed cars (they're now in database for next time)
        seedCars
    }

    /**
     * Add Car: Save a new car to the database
     * 
     * Flow:
     * 1. Run on background thread (Dispatchers.IO) to avoid blocking UI
     * 2. Get DAO from database
     * 3. Convert Car domain object to CarEntity database object
     * 4. Insert or update car in database (upsert)
     *    - If car with same ID exists, update it
     *    - If car doesn't exist, insert it
     * 
     * @param context: Android context for database access
     * @param car: Car object to save to database
     */
    suspend fun addCar(context: Context, car: Car) = withContext(Dispatchers.IO) {
        // Get database access object
        val carDao = dao(context)
        
        // Convert Car to CarEntity and save to database
        // upsertCar handles both insert (new) and update (existing) cases
        carDao.upsertCar(car.toEntity())
    }
}
