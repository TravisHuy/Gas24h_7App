package com.nhathuy.gas24h_7app.di.module

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.nhathuy.gas24h_7app.data.repository.SearchRepository
import com.nhathuy.gas24h_7app.di.key.ViewModelKey
import com.nhathuy.gas24h_7app.viewmodel.HomeSharedViewModel
import com.nhathuy.gas24h_7app.viewmodel.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeSharedViewModel::class)
    abstract fun bindHomeSharedViewModel(viewModel: HomeSharedViewModel):ViewModel

}