package com.example.anime.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.anime.database.getDatabase
import com.example.anime.repository.AnimeRepository
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    //private val animeRepository = AnimeRepository(database)

   init {
       viewModelScope.launch {

       }
   }

    //val animeList = animeRepository.animeItem

    /**
     * Factory for constructing HomeViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
