package com.example.zguba.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zguba.model.Car

/**
 * CarEntity: Database entity representing a car in the SQLite database
 * 
 * Purpose:
 * - Maps to the "cars" table in the database
 * - Used by Room library to store/retrieve car data
 * - Separate from domain model (Car) to keep database concerns isolated
 * 
 * @Entity annotation:
 * - tableName = "cars": Name of the database table
 * - Room will create this table automatically
 * 
 * @PrimaryKey: The id field uniquely identifies each car
 * - Used for lookups and updates
 * - Prevents duplicate cars with same ID
 */
@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String, // Unique identifier for the car
    val make: String, // Car manufacturer (e.g., "Tesla")
    val model: String, // Car model (e.g., "Model S")
    val year: Int, // Manufacturing year (e.g., 2024)
    val price: Int, // Car price in dollars (e.g., 89990)
    val imageUrl: String, // URL to car image
    val description: String // Optional car description
)

/**
 * Extension Function: Convert CarEntity (database) to Car (domain model)
 * 
 * Purpose: Convert database representation to app domain model
 * Used when: Reading cars from database to display in UI
 * 
 * @return Car: Domain model object for use in app logic
 */
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

/**
 * Extension Function: Convert Car (domain model) to CarEntity (database)
 * 
 * Purpose: Convert app domain model to database representation
 * Used when: Saving cars to database (from UI or repository)
 * 
 * @return CarEntity: Database entity object for storage
 */
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
