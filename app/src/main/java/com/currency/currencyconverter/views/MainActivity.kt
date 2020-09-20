package com.currency.currencyconverter.views

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.currency.currencyconverter.App
import com.currency.currencyconverter.R
import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyAmount
import com.currency.currencyconverter.utils.INTERVAL_TIME
import com.currency.currencyconverter.utils.StatusEnum
import com.currency.currencyconverter.utils.Worker
import com.currency.currencyconverter.viewmodels.MainActivityViewModel
import com.currency.currencyconverter.views.adapters.CurrencyDropDownAdapter
import com.currency.currencyconverter.views.adapters.ExchangeRateAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), TextWatcher {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var workerManager: WorkManager
    private lateinit var workRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val daggerComponent = (application as App).daggerComponent()
        daggerComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        workerManager = WorkManager.getInstance(this)

        currencyInputEditText.addTextChangedListener(this)
        currencyRateRecyclerView.layoutManager = LinearLayoutManager(this)
        currencyRateRecyclerView.adapter = ExchangeRateAdapter(arrayListOf())
        setSpinnerOnItemSelectedListener()

        setObservers()
        setWorkManager()
    }


    override fun onStop() {
        if (::workerManager.isInitialized && ::workRequest.isInitialized)
            workerManager.cancelAllWorkByTag(Worker::class.java.simpleName)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (::workerManager.isInitialized && ::workRequest.isInitialized)
            workerManager.enqueue(workRequest)
    }

    private fun setObservers() {
        viewModel.currencies.observe(this, Observer {
            when (it.status) {
                StatusEnum.SUCCESS -> {
                    if (it.data == null || it.data.isEmpty()) return@Observer
                    var currencyList: ArrayList<Currency> = ArrayList(it.data)
                    currencyList.add(0, Currency("", getString(R.string.select_currency)))
                    currencySpinner.adapter = CurrencyDropDownAdapter(currencyList)
                    progressBar.visibility = View.GONE
                }

                StatusEnum.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                StatusEnum.ERROR -> {
                    progressBar.visibility = View.GONE
                }
            }
        })


        viewModel.data.observe(this, Observer {
            when (it.status) {
                StatusEnum.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    if (it.data == null || it.data.isEmpty()) {
                        return@Observer
                    }

                    (currencyRateRecyclerView.adapter as ExchangeRateAdapter).setExchangeRate(it.data as ArrayList<CurrencyAmount>)
                }

                StatusEnum.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                StatusEnum.ERROR -> {
                    progressBar.visibility = View.GONE

                }
            }
        })

        viewModel.exchangeRates.observe(this, Observer {
            when (it.status) {
                StatusEnum.SUCCESS -> {
                    calculateExchangeRate()
                }

                StatusEnum.LOADING -> {

                }
                StatusEnum.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }


    private fun setWorkManager() {
        workRequest =
            PeriodicWorkRequest.Builder(Worker::class.java, INTERVAL_TIME, TimeUnit.MINUTES)
                .addTag(Worker::class.java.simpleName)
                .build()
        workerManager.enqueueUniquePeriodicWork(
            Worker::class.java.simpleName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        calculateExchangeRate()
    }

    private fun setSpinnerOnItemSelectedListener() {
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                try {
                    calculateExchangeRate()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun calculateExchangeRate() {
        if (TextUtils.isEmpty((currencySpinner.selectedItem as Currency).code) && TextUtils.isEmpty(
                currencyInputEditText.text
            )
        ) {
            return
        }
        val currencyType = currencySpinner.selectedItem as Currency
        val value = currencyInputEditText.text.toString().toDouble()
        viewModel.exchangeRateCalculate(value, currencyType)
    }

}
