package com.currency.currencyconverter.di

import android.content.Context
import com.currency.currencyconverter.utils.Worker
import com.currency.currencyconverter.views.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CurrencyProvider::class, ViewModelModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(exchangeRateWorker: Worker)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun bindContext(context: Context) : Builder
        fun build(): AppComponent
    }
}