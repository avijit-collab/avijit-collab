package com.currency.currencyconverter.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "currencies",
    indices = [Index( value = ["code", "name"], unique = true)]
)
data class Currency (
    @NonNull
    var code: String,
    @NonNull
    var name: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Currency

        if (code != other.code) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}