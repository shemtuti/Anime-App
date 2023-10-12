package com.example.anime.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnimeList(vararg animeList: TableAnime)

    @Query("SELECT * FROM anime_table")
    fun getAllAnime(): LiveData<List<TableAnime>>

}


