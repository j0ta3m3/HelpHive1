package com.example.helphive1.ui.composable.search

import PublicacionRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helphive1.model.Publication
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val publicacionRepository = PublicacionRepository()

    private val _searchResults = MutableLiveData<List<Publication>>()
    val searchResults: LiveData<List<Publication>> get() = _searchResults

    fun searchPublicationsByPartialName(partialName: String) {
        if (partialName.isNotEmpty()) {
            publicacionRepository.getPublicacionesByPartialName(
                partialName,
                onSuccess = { publications ->
                    _searchResults.value = publications
                },
                onFailure = { errorMessage ->
                    // Manejar el error aquí
                }
            )
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun searchPublicationsByLocation(location: String) {
        viewModelScope.launch {
            try {
                val results = publicacionRepository.searchPublicationsByLocation(location)
                _searchResults.value = results
            } catch (e: Exception) {
                // Manejar el error de búsqueda
                _searchResults.value = emptyList()
            }
        }
    }
}