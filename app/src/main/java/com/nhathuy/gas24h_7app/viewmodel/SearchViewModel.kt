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

    fun searchProducts(query:String){
        viewModelScope.launch {
            _isLoading.value=true
            val result = searchRepository.searchProducts(query)
            result.fold(
                onSuccess ={products ->
                    _searchResults.value = products
                    saveRecentSearch(query)
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

}