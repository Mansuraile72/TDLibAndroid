// पथ: app/src/main/java/com/genius/tdlibandroid/data/tgRepos/TgUserRepository.kt
package com.genius.tdlibandroid.data.tgRepos

import com.genius.tdlibandroid.data.TelegramClient
import org.drinkless.tdlib.TdApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TgUserRepository @Inject constructor(
    private val telegramClient: TelegramClient
) {
    /** प्रोफ़ाइल यूज़र */
    suspend fun getMe(): TdApi.User = telegramClient.getMe()

    /** चैट लिस्ट */
    suspend fun getChats(limit: Int): List<TdApi.Chat> = telegramClient.getChats(limit)

    /** कोई एक चैट */
    suspend fun getChat(chatId: Long): TdApi.Chat = telegramClient.getChat(chatId)

    /** आगे ज़रूरत हो तो आगे के फ़ंक्शन डेलीगेट करें:
     * searchChats, openChat, closeChat, getChatHistory, viewMessages, sendTyping,
     * sendTextMessage, editMessageText, deleteMessages, downloadFile, getFile, …
     * —> सबको telegramClient पर फॉरवर्ड करें.
     */
}