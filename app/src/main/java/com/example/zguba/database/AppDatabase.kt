package com.example.zguba.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zguba.database.dao.LikedCarDao
import com.example.zguba.database.dao.UserDao
import com.example.zguba.database.entity.LikedCar
import com.example.zguba.database.entity.User

@Database(
    entities = [User::class, LikedCar::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun likedCarDao(): LikedCarDao
}

