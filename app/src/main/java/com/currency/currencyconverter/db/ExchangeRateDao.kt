package com.currency.currencyconverter.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.currency.currencyconverter.models.CurrencyExchangeRate

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExchangeRate(exchangeRate: CurrencyExchangeRate)

    @Query("SELECT * FROM exchange_rates LIMIT 1")
    fun getExchangeRate(): CurrencyExchangeRate?

    @Query("DELETE FROM exchange_rates")
    fun deleteExchangeRate(): Int
}