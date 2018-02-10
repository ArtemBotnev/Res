package ru.artembotnev.res.network

import android.os.AsyncTask

import ru.artembotnev.res.datamanagement.StockManager

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Artem on 09.02.2018.
 * Provider JSON string
 */

class JsonProvider : AsyncTask<Void, Void, String?>() {
    companion object {
        private const val DATA_URL = "http://phisix-api3.appspot.com/stocks.json"
    }

    var connection: HttpURLConnection? = null
    var reader: BufferedReader? = null

    override fun doInBackground(vararg params: Void?): String? {
        var result: String? = null

        try {
            result = readJson()
        } catch (e: IOException) {
            e.stackTrace
        } finally {
            reader?.close()
        }

        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        StockManager.parse(result)
    }

    /**
     * Obtainment json string from source
     */
    private fun readJson(): String {
        val url = URL(DATA_URL)
        connection = url.openConnection() as HttpURLConnection
        if (connection == null) throw IOException()
        connection!!.requestMethod = "GET"
        connection!!.connect()

        if (connection!!.responseCode != HttpURLConnection.HTTP_OK) {
            val message = connection!!.responseMessage
            throw IOException("$message: with $DATA_URL")
        }

        reader = BufferedReader(InputStreamReader(connection!!.inputStream))

        val builder = StringBuilder()
        while (true) {
            val line = reader!!.readLine() ?: break
            builder.append(line)
        }

        return builder.toString()
    }
}