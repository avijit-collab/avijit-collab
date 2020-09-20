package com.currency.currencyconverter.utils

import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyAmount
import com.currency.currencyconverter.models.CurrencyExchangeRate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class ExchangeRateCalculator (
    var exchangeRates: CurrencyExchangeRate,
    var currencies: List<Currency>,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO
){
    /**
     * Convert entered amount to source currency
     * @param amount
     * @param currency
     * @param callback callback to notify after done the task
     */
    suspend fun convertToSourceCurrency(amount: Double, currency: Currency, callback: (List<CurrencyAmount>) -> Unit) =
        withContext(dispatchers){
            try{
                val sourceAmt = convertToSourceAmt(amount, currency)
                val list = arrayListOf<CurrencyAmount>()
                for (entry: Map.Entry<String, Double> in exchangeRates.quotes){
                    val key = entry.key.substring(3)
                    val entryCurrency = findCurrency(key) ?: continue
                    list.add(CurrencyAmount( sourceAmt * entry.value, entryCurrency ))
                }
                callback.invoke(list)
            }catch (ex: Exception){
                callback.invoke(listOf())
            }
    }

    private fun convertToSourceAmt(amt: Double, currency: Currency): Double{
        val key = "${exchangeRates.source}${currency.code}"
        val rate = exchangeRates.quotes[key] ?:
        kotlin.run { throw Exception(CURRENCY_NOT_EXISTS) }
        return amt / rate
    }
    private fun findCurrency(code: String): Currency? =
        currencies.find { it.code == code }
}