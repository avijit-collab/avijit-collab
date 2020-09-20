package com.currency.currencyconverter.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Exchange rate map to string
 * and string to map converter
 * */
object ExchangeRatesMapTypeConverter {
    @TypeConverter
    @JvmStatic
    fun convertStringToMap(value: String): Map<String, Double> {
        return Gson().fromJson(value, object : TypeToken<Map<String, Double>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun convertMapToString(value: Map<String, Double>?): String {
        return if (value == null) "" else Gson().toJson(value)
    }
}