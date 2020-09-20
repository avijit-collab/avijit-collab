package com.currency.currencyconverter.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.currency.currencyconverter.R
import com.currency.currencyconverter.models.Currency

class CurrencyDropDownAdapter(var currencyList: List<Currency>) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.currency_drop_down_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.currencyText)
        textView.text = "${currencyList[position].name}"
        return view

    }

    override fun getItem(position: Int): Any? {
        return currencyList[position]
    }

    override fun getCount(): Int {
        return currencyList.size
    }

    override fun getItemId(position: Int): Long {
        return if (currencyList[position].id < 1) position.toLong()
        else currencyList[position].id


    }
}