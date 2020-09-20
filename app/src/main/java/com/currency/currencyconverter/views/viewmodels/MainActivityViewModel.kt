package com.currency.currencyconverter.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currency.currencyconverter.interfaces.ICurrencyRepo
import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyAmount
import com.currency.currencyconverter.models.CurrencyExchangeRate
import com.currency.currencyconverter.utils.Data
import com.currency.currencyconverter.utils.ExchangeRateCalculator
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

open class MainActivityViewModel
@Inject constructor(
    private val repoI: ICurrencyRepo
): ViewModel() {

    open val currencies: MutableLiveData<Data<List<Currency>>> = MutableLiveData()

    open val exchangeRates: MutableLiveData<Data<CurrencyExchangeRate?>> = MutableLiveData()

    open val data: MutableLiveData<Data<List<CurrencyAmount>?>> = MutableLiveData()

    var exchangeRateCalculator: ExchangeRateCalculator? = null

    private lateinit var calcCurrencies: List<Currency>

    init {
        init()
    }

    private fun init(){
        loadCurrencies {
            initExchangeRates()
        }
    }

    private fun loadCurrencies(initExchangeRate: () -> Unit){
        //load the currencies first
        currencies.postValue( Data.loading(currencies.value?.data))
        viewModelScope.launch {
            try{
                val values = repoI.loadCurrencies()
                if(values.isEmpty())
                    currencies.postValue(Data.error(currencies.value?.data ?: listOf(), "Server returned empty data"))
                else {
                    currencies.postValue(Data.success(values))
                    calcCurrencies = values
                    initExchangeRate.invoke()
                }
            }catch (ex: Exception){
                currencies.postValue(Data.error(currencies.value?.data ?: listOf(), ex.message ?: "unknown error"))
            }
        }
    }

    private fun initExchangeRates(){
        exchangeRates.postValue( Data.loading(exchangeRates.value?.data))
        viewModelScope.launch {
            try {
                val callback: (CurrencyExchangeRate?) -> Unit = {
                    if(it == null)
                        exchangeRates.postValue(Data.error(exchangeRates.value?.data, "Server returned empty data"))
                    else {
                        exchangeRates.postValue(Data.success(it))
                        initCalculator(it)
                    }
                }
                repoI.loadExchangeRates(callback)
                repoI.setExchangeRateCallBack(callback)
            } catch (ex: Exception) {
                exchangeRates.postValue( Data.error(exchangeRates.value?.data, ex.message ?: "unknown error"))
            }
        }
    }

    private fun initCalculator(exchangeRates: CurrencyExchangeRate){
        if(exchangeRateCalculator == null)
            exchangeRateCalculator = ExchangeRateCalculator( exchangeRates, calcCurrencies)
        else{
            exchangeRateCalculator!!.exchangeRates = exchangeRates
            exchangeRateCalculator!!.currencies = calcCurrencies
        }
    }

    fun calculate(amount: Double, currency: Currency) {
        if(exchangeRateCalculator == null){
            data.postValue( Data.error(null, "unable to calculate exchange rates"))
            return
        }
        data.postValue( Data.loading(null))
        viewModelScope.launch {
            exchangeRateCalculator!!.calculate(amount, currency) {
                data.postValue( Data.success(it))
            }
        }
    }
}