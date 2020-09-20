package com.currency.currencyconverter.models


data class CurrencyResponse (
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val currencies: Map<String, String>
)