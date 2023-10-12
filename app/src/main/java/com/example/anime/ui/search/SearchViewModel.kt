package com.example.anime.ui.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.anime.database.getDatabase
import com.example.anime.repository.AnimeRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    private val animeRepository = AnimeRepository(database)

    private val _searchAnimeListLiveData = MutableLiveData<String>()
    val searchAnimeListLiveData: LiveData<String> = _searchAnimeListLiveData

    @Suppress("TooGenericExceptionCaught")
    fun uploadImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                val searchAnimeContainer = animeRepository.searchImage(imageBytes)
                _searchAnimeListLiveData.postValue(searchAnimeContainer)

            } catch (e: Exception) {
                Timber.i("RESPONSE-EXCEPTION + $e")
            }
        }
    }


    /**
     * Factory for constructing HomeViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
