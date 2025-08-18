// पथ: app/src/main/java/com/genius/tdlibandroid/data/TgCore.kt
package com.genius.tdlibandroid.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TelegramClient के चारों ओर एक पतला रैपर।
 * पुराना TDLib कोड हटा दिया गया है।
 */
@Singleton
class TgCore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val telegramClient: TelegramClient
) {
    // authState को सीधे telegramClient से एक्सपोज करें
    val authState: StateFlow<AuthState> get() = telegramClient.authState

    // यदि आप यहां कुछ "initialize" करते थे, तो उसे अभी खाली छोड़ दें
    fun initialize() {
        // अभी के लिए खाली
    }

    // आवश्यकता पड़ने पर बाद में हेल्पर फ़ंक्शंस जोड़ें, जैसे:
    // suspend fun setPhone(phone: String) = telegramClient.setPhoneNumber(phone)
}