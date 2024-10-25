package com.nhathuy.gas24h_7app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository):ViewModel(){

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResult: StateFlow<List<Product>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<Product>>(emptyList())
    val recentSearches: StateFlow<List<Product>> = _recentSearches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var originalResults = listOf<Product>()
    private var currentSearchQuery = ""
    private var currentRating: Float? = null

    fun searchProducts(query:String){
        viewModelScope.launch {
            _isLoading.value=true
            val result = searchRepository.searchProducts(query)
            result.fold(
                onSuccess ={products ->
                    originalResults = products.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true)
                    }
                    applyCurrentFilters()
                },
                onFailure = {

                }
            )
            _isLoading.value =false
        }
    }
    fun getRecentSearches(){
        viewModelScope.launch {
            val result = searchRepository.getRecentSearches()
            result.fold(
                onSuccess = { searches->
                    _recentSearches.value = searches
                },
                onFailure = {e->
                    _error.value = e.message?: "Failed to get recent searches"
                }
            )
        }
    }

    private fun saveRecentSearch(query: String) {
        viewModelScope.launch {
            searchRepository.saveRecentSearch(query)
        }
    }

    fun sortByBestSeller() {
        _searchResults.value = _searchResults.value.sortedByDescending { it.soldCount }
    }

    fun sortByHighPrice() {
        _searchResults.value = _searchResults.value.sortedByDescending { it.getDiscountedPrice() }
    }

    fun sortByLowPrice() {
        _searchResults.value = _searchResults.value.sortedBy { it.getDiscountedPrice() }
    }

    fun sortByRelevance() {
        applyCurrentFilters()
    }

    fun filterByRating(rating: Float) {
        currentRating = rating
        applyCurrentFilters()
    }

    fun resetRatingFilter() {
        currentRating = null
        applyCurrentFilters()
    }


    private fun applyCurrentFilters() {
        var filteredResults = originalResults

        // Apply rating filter if exists
        currentRating?.let { rating ->
            filteredResults = filteredResults.filter {
                when (rating) {
                    5f -> it.averageRating >= 4.5f
                    4f -> it.averageRating >= 3.5f && it.averageRating < 4.5f
                    3f -> it.averageRating >= 2.5f && it.averageRating < 3.5f
                    2f -> it.averageRating >= 1.5f && it.averageRating < 2.5f
                    else -> it.averageRating < 1.5f
                }
            }
        }

        _searchResults.value = filteredResults
    }

    fun clearSearch() {
        viewModelScope.launch {
            _searchResults.emit(emptyList())
            currentRating = null
            originalResults = emptyList()
            _isLoading.emit(false)
            _error.emit(null)
        }
    }
}