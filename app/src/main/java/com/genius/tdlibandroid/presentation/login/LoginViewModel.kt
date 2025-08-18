// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/login/LoginViewModel.kt
package com.genius.tdlibandroid.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val telegramClient: TelegramClient
) : ViewModel() {

    // TelegramClient से सीधे authState को लेना
    val authState = telegramClient.authState

    // UI स्टेट के लिए Flows
    val phoneNumber = MutableStateFlow("")
    val code = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun sendPhoneNumber() {
        viewModelScope.launch {
            if (phoneNumber.value.isNotBlank()) {
                try {
                    telegramClient.setPhoneNumber(phoneNumber.value)
                } catch (e: Exception) {
                    // यहाँ एरर हैंडलिंग की जा सकती है
                    e.printStackTrace()
                }
            }
        }
    }

    fun sendCode() {
        viewModelScope.launch {
            if (code.value.isNotBlank()) {
                try {
                    telegramClient.checkCode(code.value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun sendPassword() {
        viewModelScope.launch {
            if (password.value.isNotBlank()) {
                try {
                    telegramClient.checkPassword(password.value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}