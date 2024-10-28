package com.nhathuy.gas24h_7app.ui.search

import com.nhathuy.gas24h_7app.viewmodel.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val searchViewModel: SearchViewModel):SearchContract.Presenter{

    private var view:SearchContract.View? =null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main+job)

    override fun attachView(view: SearchContract.View) {
        this.view=view
        observeViewModel()
    }


    override fun detachView() {
        view = null
        job.cancel()
    }

    override fun searchProducts(query: String) {
        view?.showLoading()
        searchViewModel.searchProducts(query)
    }

    override fun sortByBestSeller() {
        searchViewModel.sortByBestSeller()
    }

    override fun sortByHighPrice() {
        searchViewModel.sortByHighPrice()
    }

    override fun sortByLowPrice() {
        searchViewModel.sortByLowPrice()
    }

    override fun sortByRelevance() {
        searchViewModel.sortByRelevance()
    }

    override fun filterByRating(rating: Float) {
        searchViewModel.filterByRating(rating)
    }

    override fun resetStarFilter() {
        searchViewModel.resetRatingFilter()
    }

    override fun clearSearch() {
        searchViewModel.clearSearch()
    }

    //    override fun getRecentSearches() {
//        searchViewModel.getRecentSearches()
//    }
    private fun observeViewModel() {
        coroutineScope.launch {
            searchViewModel.searchResult.collect{
                products->
                view?.hideLoading()
                view?.showSearchResults(products)
            }
        }

//        coroutineScope.launch {
//            searchViewModel.recentSearches.collect { searches ->
//                view?.showRecentSearches(searches)
//            }
//        }

        coroutineScope.launch {
            searchViewModel.isLoading.collect { isLoading ->
                if (isLoading) view?.showLoading() else view?.hideLoading()
            }
        }

        coroutineScope.launch {
            searchViewModel.error.collect { error ->
                error?.let { view?.showMessage(it) }
            }
        }
    }
}