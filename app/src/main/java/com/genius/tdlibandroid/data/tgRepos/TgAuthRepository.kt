// पथ: app/src/main/java/com/genius/tdlibandroid/data/tgRepos/TgAuthRepository.kt
package com.genius.tdlibandroid.data.tgRepos

import com.genius.tdlibandroid.data.AuthState
import com.genius.tdlibandroid.data.TelegramClient
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TgAuthRepository @Inject constructor(
    private val telegramClient: TelegramClient
) {
    /** Auth state को बाहर एक्सपोज़ करें (UI इसे observe करेगी) */
    val authState: StateFlow<AuthState> get() = telegramClient.authState

    /** फोन नम्बर सेट करना */
    suspend fun setPhoneNumber(phone: String) {
        telegramClient.setPhoneNumber(phone)
    }

    /** OTP/कोड वेरीफाई करना */
    suspend fun checkCode(code: String) {
        telegramClient.checkCode(code)
    }

    /** पासवर्ड (2FA) वेरीफाई करना */
    suspend fun checkPassword(password: String) {
        telegramClient.checkPassword(password)
    }

    /** लॉगआउट */
    suspend fun logOut() {
        telegramClient.logOut()
    }
}