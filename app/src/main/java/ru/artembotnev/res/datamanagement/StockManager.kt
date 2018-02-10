package ru.artembotnev.res.datamanagement

import android.content.Context
import android.app.AlarmManager
import android.os.Build
import android.app.PendingIntent

import com.google.gson.JsonParser

import ru.artembotnev.res.network.JsonProvider

import java.util.*


/**
 * Created by Artem on 09.02.2018.
 * Manager stocks and parser json
 */
object StockManager {
    private const val NAME = "name" // stock name
    private const val VOLUME = "volume" // stock volume
    private const val PRICE = "price" // stock volume
    private const val AMOUNT = "amount" // stock amount

    private const val REPEAT = "ru.artembotnev.res.WAKE"
    private const val INTENT_NUMBER = 1212
    private const val DELAY = 15 // delay between repeats (seconds)

    val stocks: MutableList<Stock> = ArrayList()
    var dataReceiver: DataReceiver? = null

    /**
     * parse data from json and create stocks' instance
     */
    fun parse(dataString: String?) {
        if (dataString == null) return
        if (!stocks.isEmpty()) stocks.clear() // clear the list before updating

        val jsonTree = JsonParser().parse(dataString).asJsonObject
        val stockArray = jsonTree.get("stock").asJsonArray
        stockArray
                .map { it.asJsonObject }
                .forEach {
                    with(it) {
                        val name = get(NAME).toString()
                        val volume = get(VOLUME).toString()

                        val price = get(PRICE)
                        val amount = price.asJsonObject.get(AMOUNT).toString()

                        val stock = Stock(name, volume.toInt(), amount.toDouble())
                        stocks.add(stock)
                    }
                }

        // update receiver
        dataReceiver?.update() ?: return
    }

    /**
     * receive data and set repeat
     */
    fun request(context: Context) {
        if (dataReceiver == null) return
        JsonProvider().execute()
        repeat(context)
    }

    private fun repeat(context: Context) {
        // create intent for receiver
        val intent = RepeatReceiver.createIntent(context, REPEAT)
        val pendingIntent = PendingIntent.getBroadcast(context, INTENT_NUMBER,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // create alarm manager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // set repeat time
        val currentTime = Calendar.getInstance().timeInMillis
        val repeatTime = currentTime + DELAY * 1000

        checkAPIVersion(alarmManager, repeatTime, pendingIntent)
    }

    /**
     * version check
     */
    private fun checkAPIVersion(alarm: AlarmManager,
                                time: Long, pIntent: PendingIntent) {
        when {
            Build.VERSION.SDK_INT < 19 -> alarm.set(AlarmManager.RTC_WAKEUP, time,
                    pIntent)
            Build.VERSION.SDK_INT in 19..23 -> alarm.setExact(AlarmManager.RTC_WAKEUP, time,
                    pIntent)
            Build.VERSION.SDK_INT > 23 -> alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time,
                    pIntent)
        }
    }

    /**
     * update recipient's view
     */
    interface DataReceiver {
        fun update()
    }
}