package ru.artembotnev.res.datamanagement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Artem on 09.02.2018.
 * Receiver for data refreshing
 */
class RepeatReceiver : BroadcastReceiver() {
    companion object {
        fun createIntent(context: Context, action: String): Intent {
            val intent = Intent(context, RepeatReceiver::class.java)
            intent.action = action
            return intent
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        StockManager.request(context) //repeat receiving data
    }
}