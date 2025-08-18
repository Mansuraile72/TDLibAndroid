package com.genius.tdlibandroid.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.TelegramClient
import com.genius.tdlibandroid.data.tgRepos.TgChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

// ⭐ UI State में चैट का टाइटल और दूसरे यूज़र की ID जोड़ी गई
data class ChatUiState(
    val isLoading: Boolean = false,
    val messages: List<TdApi.Message> = emptyList(),
    val input: String = "",
    val error: String? = null,
    val chatTitle: String = "",
    val otherUserId: Long? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: TgChatRepository,
    private val client: TelegramClient
) : ViewModel() {

    private var chatId: Long = 0L
    private var updatesJob: Job? = null

    // ⭐ StateFlow को पब्लिक बनाने के लिए asStateFlow() का उपयोग करें
    private val _ui = MutableStateFlow(ChatUiState(isLoading = true))
    val ui: StateFlow<ChatUiState> = _ui.asStateFlow()

    fun init(chatId: Long) {
        if (this.chatId == chatId && _ui.value.messages.isNotEmpty()) return
        this.chatId = chatId

        viewModelScope.launch {
            try {
                _ui.update { it.copy(isLoading = true, error = null) }

                // ⭐⭐ चैट की जानकारी प्राप्त करें ⭐⭐
                val chat = client.getChat(chatId)
                val otherUserId = when (val type = chat.type) {
                    is TdApi.ChatTypePrivate -> type.userId
                    else -> null // ग्रुप चैट के लिए अभी null
                }

                _ui.update {
                    it.copy(
                        chatTitle = chat.title,
                        otherUserId = otherUserId
                    )
                }
                // ⭐⭐ --- ⭐⭐

                repo.openChat(chatId)
                val page = repo.getHistory(chatId, fromMessageId = 0L, limit = 50)
                _ui.update { it.copy(isLoading = false, messages = page) }
                startUpdates()
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun startUpdates() {
        updatesJob?.cancel()
        updatesJob = viewModelScope.launch {
            // ⭐⭐⭐ यहाँ 'incomingUpdates()' को 'updates' से बदला गया है ⭐⭐⭐
            client.updates.collect { upd ->
                when (upd) {
                    is TdApi.UpdateNewMessage -> {
                        if (upd.message.chatId == chatId) {
                            _ui.update { st -> st.copy(messages = (st.messages + upd.message)) }
                        }
                    }
                    is TdApi.UpdateDeleteMessages -> {
                        if (upd.chatId == chatId) {
                            val del = upd.messageIds.toSet()
                            _ui.update { st ->
                                st.copy(messages = st.messages.filterNot { it.id in del })
                            }
                        }
                    }
                    is TdApi.UpdateMessageContent -> {
                        if (upd.chatId == chatId) {
                            launch {
                                try {
                                    val freshMessage = repo.getMessage(upd.chatId, upd.messageId)
                                    _ui.update { currentState ->
                                        currentState.copy(
                                            messages = currentState.messages.map {
                                                if (it.id == freshMessage.id) freshMessage else it
                                            }
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onInputChange(text: String) {
        _ui.update { it.copy(input = text) }
        viewModelScope.launch { repo.sendTyping(chatId) }
    }

    fun sendMessage() {
        val text = _ui.value.input.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            try {
                repo.sendText(chatId, text, replyTo = null)
                _ui.update { it.copy(input = "") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        updatesJob?.cancel()
        viewModelScope.launch { repo.closeChat(chatId) }
    }
}