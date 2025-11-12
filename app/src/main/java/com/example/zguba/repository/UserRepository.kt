package com.example.zguba.repository

import com.example.zguba.database.AppDatabase
import com.example.zguba.database.entity.LikedCar
import com.example.zguba.database.entity.User
import com.example.zguba.model.Car
import kotlinx.coroutines.flow.Flow

class UserRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val likedCarDao = database.likedCarDao()
    
    suspend fun createUser(username: String, email: String, password: String): Result<Long> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                return Result.failure(Exception("Username already exists"))
            }
            
            // Check if email already exists
            val existingEmail = userDao.getUserByEmail(email)
            if (existingEmail != null) {
                return Result.failure(Exception("Email already exists"))
            }
            
            val user = User(
                username = username,
                email = email,
                password = password
            )
            val userId = userDao.insertUser(user)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val user = userDao.login(username, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun likeCar(userId: Long, car: Car) {
        val existing = likedCarDao.isCarLiked(userId, car.id)
        if (existing == null) {
            val likedCar = LikedCar(
                userId = userId,
                carId = car.id,
                carMake = car.make,
                carModel = car.model,
                carYear = car.year,
                carPrice = car.price,
                carImageUrl = car.imageUrl,
                carDescription = car.description
            )
            likedCarDao.insertLikedCar(likedCar)
        }
    }
    
    suspend fun unlikeCar(userId: Long, carId: String) {
        likedCarDao.removeLikedCar(userId, carId)
    }
    
    fun getLikedCars(userId: Long): Flow<List<LikedCar>> {
        return likedCarDao.getLikedCarsByUserId(userId)
    }
    
    fun getLikedCarCount(userId: Long): Flow<Int> {
        return likedCarDao.getLikedCarCount(userId)
    }
}

