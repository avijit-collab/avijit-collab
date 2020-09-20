package com.currency.currencyconverter.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.currency.currencyconverter.utils.ExchangeRatesMapTypeConverter

@Entity(tableName = "exchange_rates")
data class CurrencyExchangeRate (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val timestamp: String,
    val source: String,
    @TypeConverters(ExchangeRatesMapTypeConverter::class)
    val quotes: Map<String, Double>
)