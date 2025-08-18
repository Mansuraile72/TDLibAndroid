package com.genius.tdlibandroid.data

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.drinkless.tdlib.TdApi

interface TelegramClient {
    val authState: StateFlow<AuthState>
    val updates: SharedFlow<TdApi.Update>

    suspend fun send(function: TdApi.Function<*>): TdApi.Object

    suspend fun setPhoneNumber(phoneNumber: String)
    suspend fun checkCode(code: String)
    suspend fun checkPassword(password: String)
    suspend fun logOut()

    suspend fun getMe(): TdApi.User
    suspend fun getUserFullInfo(userId: Long): TdApi.UserFullInfo

    suspend fun getChats(limit: Int): List<TdApi.Chat>
    suspend fun getChat(chatId: Long): TdApi.Chat
    suspend fun searchChats(query: String, limit: Int): List<TdApi.Chat>

    suspend fun openChat(chatId: Long)
    suspend fun closeChat(chatId: Long)

    // ⭐ getChatHistory को 5 आर्ग्यूमेंट्स के साथ ठीक किया गया
    suspend fun getChatHistory(
        chatId: Long,
        fromMessageId: Long,
        offset: Int,
        limit: Int,
        onlyLocal: Boolean
    ): Array<TdApi.Message>

    suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message

    // ⭐ viewMessages को 3 आर्ग्यूमेंट्स के साथ ठीक किया गया
    suspend fun viewMessages(chatId: Long, messageIds: LongArray, forceRead: Boolean)

    suspend fun sendTyping(chatId: Long)

    suspend fun sendTextMessage(
        chatId: Long, text: String, replyTo: Long?
    ): TdApi.Message

    suspend fun editMessageText(
        chatId: Long,
        messageId: Long,
        text: TdApi.InputMessageText
    ): TdApi.Message

    suspend fun deleteMessages(chatId: Long, messageIds: LongArray, revoke: Boolean)
    suspend fun downloadFile(fileId: Int, priority: Int = 1, synchronous: Boolean = true): TdApi.File
    suspend fun getFile(fileId: Int): TdApi.File
}