package com.example.anime.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.anime.database.AppDatabase
import com.example.anime.database.asDomainModel
import com.example.anime.domain.Anime
import com.example.anime.service.Network
import com.example.anime.service.NetworkSearch
import com.example.anime.service.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

class AnimeRepository(private val database: AppDatabase) {

    // Fetch animation data from Room
    val animes: LiveData<List<Anime>> = database.roomDao.getAllAnime().map {
        it.asDomainModel()
    }

    // Fetch animation data from API and save in Room
    @Suppress("TooGenericExceptionCaught", "SpreadOperator")
    suspend fun refreshTopAnime() {
        withContext(Dispatchers.IO) {
            try{
                //val response = Network.animeBytes.getTopAnime(1, 25).await()
                val response = Network.animeBytes.getTopAnime().await()
                database.roomDao.insertAnimeList(*response.asDatabaseModel())
            }
            catch (e:Exception){
                Timber.i("DATA-ANIME: ${e.message}")
            }
        }
    }


    // Fetch image data response from API
    @Suppress("TooGenericExceptionCaught")
    suspend fun searchImage(imageBytes: ByteArray): String {
        return withContext(Dispatchers.IO){
            try{
                val requestBody =
                    imageBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, imageBytes.size)
                val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

                val response = NetworkSearch.searchBytes.uploadImage(imagePart).await()
                Log.e("##SEARCH-REPO-1: ", "$response")
                return@withContext response.toString()
            } catch (e:Exception) {
                // Log the exception
                println("Exception occurred: ${e.message}")
            }
        }.toString()
    }

}




