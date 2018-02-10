package ru.artembotnev.res

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*

import ru.artembotnev.res.datamanagement.Stock
import ru.artembotnev.res.datamanagement.StockManager

import java.util.*

class MainActivity : AppCompatActivity(), StockManager.DataReceiver {
    private var stockAdapter: StockAdapter? = null

    companion object {
        fun newInstance(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StockManager.dataReceiver = this // appointment StockManager.DataReceiver
        StockManager.request(this) // data request

        recycler.layoutManager = LinearLayoutManager(this)
        updateUI()
    }

    override fun onDestroy() {
        StockManager.dataReceiver = null
        super.onDestroy()
    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item!!.itemId == R.id.refresh) {
            StockManager.request(this)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    // fun of StockManager.DataReceiver
    override fun update() {
        updateUI()
    }

    private fun updateUI() {
        val stocks = StockManager.stocks
        if (stockAdapter == null) {
            stockAdapter = StockAdapter(stocks)
            recycler.adapter = stockAdapter
        } else {
            stockAdapter!!.stocks = stocks
            stockAdapter!!.notifyDataSetChanged()
        }
    }

    //View holder
    private inner class StockHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.currency_cell, parent, false)) {

        fun bing(item: Stock) {
            val name = itemView.findViewById<TextView>(R.id.stock_name)
            val volume = itemView.findViewById<TextView>(R.id.stock_volume)
            val amount = itemView.findViewById<TextView>(R.id.stock_amount)

            name.text = item.name
            volume.text = item.volume.toString()
            amount.text = String.format(Locale.ENGLISH, "%.2f", item.amount)
        }
    }

    //Adapter
    private inner class StockAdapter(var stocks: List<Stock>) :
            RecyclerView.Adapter<StockHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StockHolder {
            val inflater = LayoutInflater.from(this@MainActivity)

            return StockHolder(inflater, parent!!)
        }

        override fun onBindViewHolder(holder: StockHolder?, position: Int) {
            holder!!.bing(stocks[position])
        }

        override fun getItemCount(): Int = stocks.size
    }
}