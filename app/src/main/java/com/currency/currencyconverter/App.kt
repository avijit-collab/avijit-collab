package com.currency.currencyconverter

import android.app.Application
import com.currency.currencyconverter.di.AppComponent
import com.currency.currencyconverter.di.DaggerAppComponent


open class App : Application() {
    private lateinit var daggerComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        daggerComponent = DaggerAppComponent
            .builder()
            .bindContext(this)
            .build()
    }

    open fun daggerComponent(): AppComponent = daggerComponent
}