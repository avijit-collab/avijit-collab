package com.currency.currencyconverter.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.currency.currencyconverter.R
import com.currency.currencyconverter.models.Currency
import com.currency.currencyconverter.models.CurrencyAmount
import com.currency.currencyconverter.utils.display
import kotlinx.android.synthetic.main.exchage_rate_item.view.*
import okhttp3.internal.Util

class ExchangeRateAdapter(private var currencyRateList: ArrayList<CurrencyAmount>? = null) :
    RecyclerView.Adapter<ExchangeRateAdapter.ExchangeRateViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        return ExchangeRateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.exchage_rate_item, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return currencyRateList?.size ?: 0

    }

    /**
     * Update data for recycler adapter*/
    fun setExchangeRate(rates: ArrayList<CurrencyAmount>) {
        currencyRateList = rates
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        holder.bindItem(currencyRateList?.get(position))
    }

    inner class ExchangeRateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindItem(currencyAmount: CurrencyAmount?) {
            currencyAmount?.let {
                itemView.countryName.text = currencyAmount.currency?.name
                itemView.amountText.text = display(currencyAmount)
            }

        }
    }
}