package com.nhathuy.gas24h_7app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.repository.SearchRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository):ViewModel(){

    private val _searchResults = MutableLiveData<List<Product>>()
    val searchResult : LiveData<List<Product>> = _searchResults

    private val _recentSearches = MutableLiveData<List<Product>>()
    val recentSearches : LiveData<List<Product>> = _recentSearches

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error

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