package com.example.anime.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.anime.database.getDatabase
import com.example.anime.repository.AnimeRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    private val animeRepository = AnimeRepository(database)

   init {
       viewModelScope.launch {
           animeRepository.refreshTopAnime()
       }
   }

    val animeList = animeRepository.animes

    //
    fun refreshData() {
        viewModelScope.launch {
            animeRepository.refreshTopAnime()
        }
    }

    /**
     * Factory for constructing HomeViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
