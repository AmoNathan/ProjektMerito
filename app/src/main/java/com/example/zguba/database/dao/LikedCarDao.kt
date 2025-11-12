package com.example.zguba.database.dao

import androidx.room.*
import com.example.zguba.database.entity.LikedCar
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedCarDao {
    @Query("SELECT * FROM liked_cars WHERE userId = :userId")
    fun getLikedCarsByUserId(userId: Long): Flow<List<LikedCar>>
    
    @Query("SELECT * FROM liked_cars WHERE userId = :userId AND carId = :carId LIMIT 1")
    suspend fun isCarLiked(userId: Long, carId: String): LikedCar?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedCar(likedCar: LikedCar): Long
    
    @Delete
    suspend fun deleteLikedCar(likedCar: LikedCar)
    
    @Query("DELETE FROM liked_cars WHERE userId = :userId AND carId = :carId")
    suspend fun removeLikedCar(userId: Long, carId: String)
    
    @Query("SELECT COUNT(*) FROM liked_cars WHERE userId = :userId")
    fun getLikedCarCount(userId: Long): Flow<Int>
}

