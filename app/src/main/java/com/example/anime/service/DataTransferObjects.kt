package com.example.anime.service

import com.example.anime.database.TableAnime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkAnimeContainer(val data: List<NetworkAnime>)

@JsonClass(generateAdapter = true)
data class SearchAnimeContainer(val result: List<SearchAnime>)

@JsonClass(generateAdapter = true)
@Suppress("ConstructorParameterNaming")
data class NetworkAnime(
    val mal_id: Int,
    val title: String,
    val year: Int?,
    val episodes: Int,
    val score: Float,
    val duration: String,
    val trailer: YoutubeUrl,
    val synopsis: String,
    val rating: String,
    val source: String,
    val images: Images
)

data class Images(
    val jpg: JpgImages
)
@Suppress("ConstructorParameterNaming")
data class JpgImages(
    val large_image_url: String
)

@Suppress("ConstructorParameterNaming")
data class YoutubeUrl(
    val youtube_id: String?
)

// Convert Network results to database objects
fun NetworkAnimeContainer.asDatabaseModel() : Array<TableAnime> {
    return data.map {
        TableAnime(
            malId = it.mal_id,
            title = it.title,
            year = it.year,
            episodes = it.episodes,
            score = it.score,
            duration = it.duration,
            trailerUrl = it.trailer.youtube_id.toString(),
            synopsis = it.synopsis,
            rating = it.rating,
            source = it.source,
            imageUrl = it.images.jpg.large_image_url
        )
    }.toTypedArray()
}

@JsonClass(generateAdapter = true)
data class SearchAnime(
    val filename: String,
    val episode: String?
) {
    private val unknown = "Unknown"

    val searchEpisode: String
        get() = (if(episode.equals("null")){
            "Episode: $unknown"
        } else {
            "Episode: $episode"
        }).toString()
}
