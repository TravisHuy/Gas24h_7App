package com.nhathuy.gas24h_7app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.gas24h_7app.data.model.Product
import com.nhathuy.gas24h_7app.data.model.ProductCategory
import com.nhathuy.gas24h_7app.data.repository.CategoryRepository
import com.nhathuy.gas24h_7app.data.repository.ProductRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

class HomeSharedViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _categories = MutableLiveData<List<ProductCategory>>()
    val categories: LiveData<List<ProductCategory>> = _categories

    private val _productCategory = MutableLiveData<Map<String, List<Product>>>()
    val productCategory: LiveData<Map<String, List<Product>>> = _productCategory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _refreshTrigger = MutableLiveData<Unit>()

    init {
        _refreshTrigger.value = Unit
    }

    fun refreshData(){
        _refreshTrigger.value = Unit
    }

    //fetchCategories
    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = categoryRepository.getCategories()
                result.fold(
                    onSuccess = { categoriesList ->
                        _categories.value = categoriesList
                        fetchProductsForAllCategories(categoriesList)
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error fetching categories"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error fetching categories"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchProductsForAllCategories(categories: List<ProductCategory>) {
        viewModelScope.launch {
            val newProductsMap = mutableMapOf<String,List<Product>>()
            categories.forEach{
                category ->
                try {
                    val result = productRepository.getProductsByCategory(category.id)
                    result.fold(
                        onSuccess = {
                            products ->
                            newProductsMap[category.id] = products
                        },
                        onFailure = { e ->
                            _error.value = e.message ?: "Error fetching products for category ${category.id}"
                        }
                    )
                }
                catch (e:Exception){
                    _error.value = e.message ?: "Error fetching products for category ${category.id}"
                }
            }
            _productCategory.value = newProductsMap
        }
    }
    //fun fetchProductForCategories

    fun fetchProductForCategory(categoryId:String){
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val  result = productRepository.getProductsByCategory(categoryId)
                result.fold(
                    onSuccess = { products ->
                        val currentMap = _productCategory.value ?: emptyMap()
                        _productCategory.value = currentMap + (categoryId to products)
                    },
                    onFailure = {e ->
                        _error.value = e.message ?: "Error fetching product for categories"
                    }
                )
            }
            catch (e:Exception){
                _error.value = e.message ?: "Error fetching product for categories"
            }
            finally {
                _isLoading.value= false
            }
        }
    }
    init {
        _refreshTrigger.observeForever {
            fetchCategories()
        }
    }
}