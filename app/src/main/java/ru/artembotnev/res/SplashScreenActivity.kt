package ru.artembotnev.res

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by Artem on 09.02.2018.
 * Launch screen
 */

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = MainActivity.newInstance(this)
        startActivity(intent)
        finish()
    }
}