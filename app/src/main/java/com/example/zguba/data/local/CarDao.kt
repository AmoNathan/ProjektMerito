package com.example.zguba.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * CarDao: Data Access Object interface for car database operations
 * 
 * Purpose:
 * - Defines SQL queries and operations for the cars table
 * - Room library generates implementation automatically
 * - All database operations go through this interface
 * 
 * @Dao annotation: Tells Room this is a Data Access Object
 * Room will generate the implementation at compile time
 */
@Dao
interface CarDao {

    /**
     * Get All Cars: Retrieve all cars from database
     * 
     * SQL Query: "SELECT * FROM cars ORDER BY rowid"
     * - SELECT *: Get all columns
     * - FROM cars: From the cars table
     * - ORDER BY rowid: Order by insertion order (rowid is auto-incrementing)
     * 
     * @return List<CarEntity>: All cars in the database
     */
    @Query("SELECT * FROM cars ORDER BY rowid")
    suspend fun getCars(): List<CarEntity>

    /**
     * Upsert Multiple Cars: Insert or update multiple cars
     * 
     * Upsert = Update if exists, Insert if new
     * 
     * OnConflictStrategy.REPLACE:
     * - If car with same ID exists, replace it with new data
     * - If car doesn't exist, insert it
     * 
     * Used for: Seeding database with initial cars
     * 
     * @param cars: List of cars to insert/update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCars(cars: List<CarEntity>)

    /**
     * Upsert Single Car: Insert or update a single car
     * 
     * OnConflictStrategy.REPLACE:
     * - If car with same ID exists, replace it with new data
     * - If car doesn't exist, insert it
     * 
     * Used for: Adding a new car from user input
     * 
     * @param car: Car to insert/update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCar(car: CarEntity)

    /**
     * Clear All Cars: Delete all cars from database
     * 
     * SQL Query: "DELETE FROM cars"
     * - Removes all rows from cars table
     * - Table structure remains (can add cars again)
     * 
     * Used for: Resetting database (not currently used in app)
     * 
     * Note: This function exists but is not called in current implementation
     */
    @Query("DELETE FROM cars")
    suspend fun clearCars()
}
