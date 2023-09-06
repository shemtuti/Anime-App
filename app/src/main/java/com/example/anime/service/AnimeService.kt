package com.example.anime.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnimeService {
    @GET("top/anime")

//    suspend fun getTopAnime(
//        @Query("page") page: Int,
//        @Query("limit") limit: Int) : Deferred<TableAnime>
//        //@Query("limit") limit: Int) : Deferred<NetworkAnimeContainer>

    fun getTopAnime(): Deferred<NetworkAnimeContainer>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jikan.moe/v4/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val animeBytes: AnimeService = retrofit.create(AnimeService::class.java)
}

interface SearchService {
    @Multipart
    @POST("search")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Deferred<SearchAnimeContainer>
}

object NetworkSearch {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.trace.moe/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val searchBytes: SearchService = retrofit.create(SearchService::class.java)
}