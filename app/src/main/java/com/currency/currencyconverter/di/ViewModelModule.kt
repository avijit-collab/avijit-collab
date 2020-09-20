package com.currency.currencyconverter.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.currency.currencyconverter.viewmodels.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule{
    @Binds
    internal abstract fun bindViewModelFactory(factory: CurrencyViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @CurrencyViewModelFactory.ViewModelKey(MainActivityViewModel::class)
    internal abstract fun MainActivityViewModel(viewModel: MainActivityViewModel): ViewModel
}