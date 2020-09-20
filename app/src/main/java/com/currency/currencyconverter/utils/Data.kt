package com.currency.currencyconverter.utils

data class Data<out T>(val status: StatusEnum, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Data<T> = Data(status = StatusEnum.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Data<T> =
            Data(status = StatusEnum.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Data<T> = Data(status = StatusEnum.LOADING, data = data, message = null)
    }
}
