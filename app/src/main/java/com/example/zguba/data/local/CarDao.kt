package com.example.zguba.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY rowid")
    suspend fun getCars(): List<CarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCars(cars: List<CarEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCar(car: CarEntity)

    @Query("DELETE FROM cars")
    suspend fun clearCars()
}

