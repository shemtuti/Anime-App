package com.example.anime.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anime.domain.Anime

// Create a Room @Entity called TableAnime
@Entity(tableName = "anime_table")
data class TableAnime(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val year: Int?,
    val episodes: Int,
    val score: Float,
    val duration: String,
    val trailerUrl: String,
    val synopsis: String,
    val rating: String,
    val source: String,
    val imageUrl: String?
)

// Function which converts from db objects to domain objects
fun List<TableAnime>.asDomainModel(): List<Anime> {
    return map {
        Anime(
            malId = it.malId,
            title = it.title,
            year = it.year,
            episodes = it.episodes,
            score = it.score,
            duration = it.duration,
            trailerUrl = it.trailerUrl,
            synopsis = it.synopsis,
            rating = it.rating,
            source = it.source,
            imageUrl = it.imageUrl
        )
    }

}

