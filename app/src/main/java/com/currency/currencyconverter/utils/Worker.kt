package com.currency.currencyconverter.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.currency.currencyconverter.App
import com.currency.currencyconverter.interfaces.ICurrencyRepo
import javax.inject.Inject

/**
 * Work manager fro periodic task
 * */
class Worker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    @Inject
    lateinit var repoI: ICurrencyRepo

    override fun doWork(): Result {
        val daggerAppComponent = (applicationContext as App).daggerComponent()
        daggerAppComponent.inject(this)
        if (!::repoI.isInitialized) return Result.retry()
        return try {
            repoI.loadExchangeRates(null, true)
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}
