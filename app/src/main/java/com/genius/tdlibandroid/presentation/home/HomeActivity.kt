// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/home/HomeActivity.kt
package com.genius.tdlibandroid.presentation.home

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.genius.tdlibandroid.R
import com.genius.tdlibandroid.data.TelegramClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var telegramClient: TelegramClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val tv = findViewById<TextView>(R.id.tvHello)

        lifecycleScope.launch {
            try {
                val user = telegramClient.getMe()
                tv.text = "Hello, ${user.firstName}!"
            } catch (e: Exception) {
                tv.text = "Error fetching user: ${e.message}"
            }
        }
    }
}