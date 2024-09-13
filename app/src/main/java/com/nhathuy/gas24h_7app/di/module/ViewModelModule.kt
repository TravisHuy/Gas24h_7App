package com.nhathuy.gas24h_7app.di.module

import androidx.lifecycle.ViewModel
import com.nhathuy.gas24h_7app.di.key.ViewModelKey
import com.nhathuy.gas24h_7app.viewmodel.HomeSharedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeSharedViewModel::class)
    abstract fun bindHomeSharedViewModel(viewModel: HomeSharedViewModel):ViewModel
}