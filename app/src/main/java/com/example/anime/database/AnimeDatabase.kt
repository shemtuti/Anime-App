package com.example.anime.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TableAnime::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract val roomDao: AnimeDao
}

// use a singleton pattern to get an instance of the database
// synchronized so it's thread safe

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java){
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "anime_db_1"
            ).build()
        }
    }
    return INSTANCE
}