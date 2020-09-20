package com.currency.currencyconverter.remote

import com.currency.currencyconverter.models.CurrencyExchangeRate
import com.currency.currencyconverter.models.CurrencyResponse
import com.currency.currencyconverter.utils.CURRENCY
import com.currency.currencyconverter.utils.EXCHANGE_RATE
import retrofit2.Call
import retrofit2.http.GET


interface ApiServices {
    @GET(EXCHANGE_RATE)
    fun getExchangeRates(): Call<CurrencyExchangeRate>
    @GET(CURRENCY)
    fun getCurrencies(): Call<CurrencyResponse>
}