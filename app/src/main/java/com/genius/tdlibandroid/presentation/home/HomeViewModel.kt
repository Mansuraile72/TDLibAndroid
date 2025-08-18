// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/home/HomeViewModel.kt
package com.genius.tdlibandroid.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val telegramClient: TelegramClient
) : ViewModel() {

    private val _chats = MutableStateFlow<List<TdApi.Chat>>(emptyList())
    val chats = _chats.asStateFlow()

    // ⭐⭐⭐ isLoading स्टेट को वापस जोड़ा गया है ⭐⭐⭐
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _isLoading.value = true // लोडिंग शुरू
            try {
                val chatList = telegramClient.getChats(limit = 20)
                _chats.value = chatList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false // लोडिंग खत्म
            }
        }
    }
}