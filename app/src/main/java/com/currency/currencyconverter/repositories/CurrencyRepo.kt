package com.currency.currencyconverter.repositories

import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyExchangeRate
import com.currency.currencyconverter.db.CurrenciesDao
import com.currency.currencyconverter.db.ExchangeRateDao
import com.currency.currencyconverter.interfaces.ICurrencyRepo
import com.currency.currencyconverter.remote.ApiServices
import com.currency.currencyconverter.utils.convertCurrencyMapToList
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CurrencyRepo
@Inject constructor(
    private var apiServices: ApiServices,
    private val currenciesDao: CurrenciesDao,
    private val exchangeRateDao: ExchangeRateDao
) : ICurrencyRepo {
    private var refreshCallback: ((CurrencyExchangeRate?) -> Unit)? = null
    var dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun loadCurrencies(forceRemote: Boolean): List<Currency> =
        withContext(dispatcher) {
            if (forceRemote)
                getCurrenciesFromApi() ?: arrayListOf()
            else {
                val cache = getCurrenciesFromDb()
                if (cache == null || cache.isNullOrEmpty()) {
                    getCurrenciesFromApi() ?: arrayListOf()
                } else
                    cache
            }
        }

    override fun loadExchangeRates(
        callback: ((CurrencyExchangeRate?) -> Unit)?,
        forceRemote: Boolean
    ) {
        GlobalScope.launch(dispatcher) {
            if (forceRemote) {
                getExchangeRateFromApi(callback)
            } else {
                val data = loadCachedExchangeRates()
                if (data == null)
                    getExchangeRateFromApi(callback)
                else
                    callback?.invoke(data)
            }
        }
    }

    override fun setExchangeRateCallBack(callback: (CurrencyExchangeRate?) -> Unit) {
        refreshCallback = callback
    }

    suspend fun loadCachedExchangeRates(): CurrencyExchangeRate? =
        withContext(dispatcher) {
            exchangeRateDao.getExchangeRate()
        }

    suspend fun getExchangeRateFromApi(callback: ((CurrencyExchangeRate?) -> Unit)?) {
        apiServices.getExchangeRates().enqueue(object : Callback<CurrencyExchangeRate?> {
            override fun onFailure(call: Call<CurrencyExchangeRate?>, t: Throwable) {
                //throw CurrencyException(t.message) //quick fix, causes app to crash in ExchangeRatesWorker --> realised late
            }

            override fun onResponse(
                call: Call<CurrencyExchangeRate?>,
                response: Response<CurrencyExchangeRate?>
            ) {
                val body = response.body()
                if (body != null) {
                    GlobalScope.launch { insertExchangeRateInToDb(body) }
                }
                refreshCallback?.invoke(body)
                callback?.invoke(body)
            }
        })
    }

    private suspend fun insertExchangeRateInToDb(data: CurrencyExchangeRate) {
        withContext(dispatcher) {
            try {
                exchangeRateDao.deleteExchangeRate()
                exchangeRateDao.insertExchangeRate(data)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
    }

    suspend fun getCurrenciesFromDb(): List<Currency>? =
        withContext(dispatcher) {
            currenciesDao.getCurrencyList()
        }

    suspend fun getCurrenciesFromApi(): List<Currency>? =
        withContext(dispatcher) {
            try {
                val resp = apiServices.getCurrencies().execute().body() ?: return@withContext null
                val list = convertCurrencyMapToList(resp.currencies)
                insertCurrenciesInToDb(list)
                return@withContext list
            } catch (e: Exception) {
                throw java.lang.Exception(e.message)
            }
        }

    private suspend fun insertCurrenciesInToDb(currencies: List<Currency>) {
        withContext(dispatcher) {
            try {
                currenciesDao.deleteCurrencyTable()
                currenciesDao.insertCurrencyList(currencies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}