package com.currency.currencyconverter.interfaces

import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyExchangeRate


interface ICurrencyRepo {
    suspend fun loadCurrencies(forceRemote: Boolean = false): List<Currency>
    fun loadExchangeRates(callback: ((CurrencyExchangeRate?) -> Unit)? = null, forceRemote: Boolean = false)
    fun setExchangeRateCallBack(callback: (CurrencyExchangeRate?) -> Unit)
}