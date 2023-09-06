package com.example.anime.domain

data class Anime(
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
    val imageUrl: String?) {

    val animeRating: String
        get() = "Rating: $rating"

    val animeScore: String
        get() = "Score: "+score?.toString() ?: "Unknown"

    private val unknown = "Unknown"

    val animeYear: String
        get() = (if(year == null){
            "Year: $unknown"
        } else {
            "Year: $year"
        }).toString()



}