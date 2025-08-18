package com.genius.tdlibandroid.data.tgRepos

import com.genius.tdlibandroid.data.TelegramClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TgChatRepository @Inject constructor(
    private val client: TelegramClient
) {
    suspend fun openChat(chatId: Long) = withContext(Dispatchers.IO) {
        client.openChat(chatId)
    }

    suspend fun closeChat(chatId: Long) = withContext(Dispatchers.IO) {
        client.closeChat(chatId)
    }

    suspend fun getHistory(
        chatId: Long,
        fromMessageId: Long = 0L,
        offset: Int = 0,
        limit: Int = 50,
        onlyLocal: Boolean = false
    ): List<TdApi.Message> = withContext(Dispatchers.IO) {
        client.getChatHistory(chatId, fromMessageId, offset, limit, onlyLocal)
            .sortedBy { it.id }
    }

    suspend fun sendText(
        chatId: Long,
        text: String,
        replyTo: Long? = null
    ): TdApi.Message = withContext(Dispatchers.IO) {
        client.sendTextMessage(chatId, text, replyTo)
    }

    suspend fun markRead(chatId: Long, messageIds: LongArray, forceRead: Boolean = true) =
        withContext(Dispatchers.IO) {
            client.viewMessages(chatId, messageIds, forceRead)
        }

    suspend fun sendTyping(chatId: Long) = withContext(Dispatchers.IO) {
        client.sendTyping(chatId)
    }

    suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message =
        withContext(Dispatchers.IO) { client.getMessage(chatId, messageId) }
}