package com.currency.currencyconverter.di

import android.content.Context
import com.currency.currencyconverter.db.AppDatabase
import com.currency.currencyconverter.interfaces.ICurrencyRepo
import com.currency.currencyconverter.remote.ApiServices
import com.currency.currencyconverter.repositories.CurrencyRepo
import com.currency.currencyconverter.utils.ACCESS_KEY
import com.currency.currencyconverter.utils.API_KEY
import com.currency.currencyconverter.utils.BASE_URL
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import dagger.Binds
import dagger.Module
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
abstract class CurrencyProvider {
    @Module
    companion object {
        @Singleton
        @JvmStatic
        @Provides
        fun provideWebService(): ApiServices {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                    OkHttpClient.Builder().addInterceptor { chain ->
                        val url = chain
                            .request()
                            .url()
                            .newBuilder()
                            .addQueryParameter(ACCESS_KEY, API_KEY)
                            .build()
                        chain.proceed(chain.request().newBuilder().url(url).build())
                    }.build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiServices::class.java)
        }

        @Singleton
        @JvmStatic
        @Provides
        fun provideExchangeRateDao(context: Context) = AppDatabase.getInstance(context).exchangeRateDao()

        @Singleton
        @JvmStatic
        @Provides
        fun provideCurrenciesDao(context: Context) = AppDatabase.getInstance(context).currenciesDao()
    }

    @Singleton
    @Binds
    abstract fun bindWeatherRepo(repoImpl: CurrencyRepo): ICurrencyRepo

}