package com.example.zguba.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * CarDatabase: Room database class for car storage
 * 
 * Purpose:
 * - Manages SQLite database connection
 * - Provides access to DAO interfaces
 * - Handles database creation and migrations
 * 
 * @Database annotation:
 * - entities = [CarEntity::class]: List of database entities (tables)
 * - version = 1: Database schema version (increment when schema changes)
 * - exportSchema = false: Don't export schema files (simplifies setup)
 */
@Database(
    entities = [CarEntity::class], // Tables in this database
    version = 1, // Schema version (increment when changing table structure)
    exportSchema = false // Don't generate schema export files
)
abstract class CarDatabase : RoomDatabase() {

    /**
     * Get Car DAO: Returns interface for car database operations
     * 
     * Room generates implementation automatically
     * 
     * @return CarDao: Interface for querying/updating cars table
     */
    abstract fun carDao(): CarDao

    /**
     * Companion Object: Contains singleton instance and factory method
     * 
     * Singleton Pattern: Only one database instance exists
     * - Prevents multiple database connections
     * - Improves performance
     * - Ensures data consistency
     */
    companion object {
        /**
         * @Volatile: Ensures visibility of INSTANCE across threads
         * - Prevents caching issues in multi-threaded environment
         * - Changes to INSTANCE are immediately visible to all threads
         */
        @Volatile
        private var INSTANCE: CarDatabase? = null

        /**
         * Get Instance: Singleton factory method
         * 
         * Double-Checked Locking Pattern:
         * 1. Check if INSTANCE exists (fast path, no locking)
         * 2. If null, synchronize block (only one thread enters)
         * 3. Check again inside synchronized block (double-check)
         * 4. If still null, create database instance
         * 5. Return the instance
         * 
         * Why this pattern?
         * - Thread-safe: Only one instance created even with multiple threads
         * - Efficient: Most calls skip synchronization (INSTANCE already exists)
         * - Prevents race conditions: Multiple threads can't create multiple instances
         * 
         * @param context: Android context needed for database file location
         * @return CarDatabase: Singleton database instance
         */
        fun getInstance(context: Context): CarDatabase {
            // Fast path: If instance exists, return it (no synchronization needed)
            return INSTANCE ?: synchronized(this) {
                // Synchronized block: Only one thread can execute this
                // Double-check: Verify instance still doesn't exist
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, // Use application context (survives activity lifecycle)
                    CarDatabase::class.java, // Database class
                    "cars.db" // Database file name (stored in app's private data directory)
                )
                    // Migration strategy: If schema changes, recreate database
                    // WARNING: This deletes all data on schema change
                    // For production, implement proper migrations
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it } // Store instance in singleton variable
            }
        }
    }
}
