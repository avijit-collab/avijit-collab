package com.currency.currencyconverter.utils

import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyAmount
import java.text.DecimalFormat
import kotlin.math.round

private val decimalFormat = DecimalFormat("#,###.##")

fun convertCurrencyMapToList(map: Map<String, String>): ArrayList<Currency> {
    val list = arrayListOf<Currency>()
    for (entry in map)
        list.add(Currency(entry.key, entry.value))
    return list
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

/**
 * Display the calculated currency
 * @param currency*/
fun display(currency: CurrencyAmount): String =
    "${currency.currency.code} ${decimalFormat.format(currency.amount.round(2))}"