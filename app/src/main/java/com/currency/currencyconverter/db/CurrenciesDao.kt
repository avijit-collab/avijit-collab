package com.currency.currencyconverter.db

import androidx.room.*
import com.currency.currencyconverter.models.Currency

@Dao
interface CurrenciesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrency(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencyList(currencies: List<Currency>)


    @Query("SELECT * FROM currencies")
    fun getCurrencyList(): List<Currency>?

    @Query("DELETE FROM currencies")
    fun deleteCurrencyTable(): Int
}