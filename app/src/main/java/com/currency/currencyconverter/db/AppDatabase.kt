package com.currency.currencyconverter.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyExchangeRate
import com.currency.currencyconverter.utils.DB_NAME
import com.currency.currencyconverter.utils.ExchangeRatesMapTypeConverter

@Database(entities = [CurrencyExchangeRate::class, Currency::class], version = 1)
@TypeConverters(value = [ExchangeRatesMapTypeConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun currenciesDao(): CurrenciesDao

    companion object {
        fun getInstance(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }
}