package com.genius.tdlibandroid.data

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class TelegramClientImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TelegramClient {

    private val client: Client
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _updates = MutableSharedFlow<TdApi.Update>(replay = 10)
    override val updates = _updates.asSharedFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    override val authState = _authState.asStateFlow()

    init {
        Client.execute(TdApi.SetLogVerbosityLevel(1))
        client = Client.create(
            { obj -> if (obj is TdApi.Update) { scope.launch { _updates.emit(obj) } } },
            { error -> Log.e("TDLib", "Update handler error: $error") },
            { ex -> Log.e("TDLib", "Default exception handler: $ex") }
        )
        scope.launch {
            updates.collect { update ->
                if (update is TdApi.UpdateAuthorizationState) {
                    handleAuthorizationState(update.authorizationState)
                }
            }
        }
    }

    private fun handleAuthorizationState(state: TdApi.AuthorizationState) {
        val newState = when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                setupTdlibParameters()
                AuthState.WaitTdlibParameters
            }
            is TdApi.AuthorizationStateWaitPhoneNumber -> AuthState.WaitPhoneNumber
            is TdApi.AuthorizationStateWaitCode -> AuthState.WaitCode
            is TdApi.AuthorizationStateWaitPassword -> AuthState.WaitPassword
            is TdApi.AuthorizationStateReady -> AuthState.Ready
            is TdApi.AuthorizationStateLoggingOut, is TdApi.AuthorizationStateClosing -> AuthState.LoggingOut
            is TdApi.AuthorizationStateClosed -> AuthState.Closed
            else -> AuthState.Error("Unsupported authorization state: $state")
        }
        _authState.value = newState
    }

    private fun setupTdlibParameters() {
        val filesDir = context.filesDir.absolutePath
        val parameters = TdApi.SetTdlibParameters()
        parameters.apiId = 27041199
        parameters.apiHash = "7f65bd72b0b483a9eddcfbc487b72420"
        parameters.databaseDirectory = "$filesDir/td_db"
        parameters.filesDirectory = "$filesDir/td_files"
        parameters.systemLanguageCode = "en"
        parameters.deviceModel = Build.MODEL
        parameters.systemVersion = Build.VERSION.RELEASE
        parameters.applicationVersion = "1.0.0"
        parameters.useMessageDatabase = true
        parameters.useFileDatabase = true
        parameters.useChatInfoDatabase = true
        parameters.useSecretChats = true
        parameters.databaseEncryptionKey = ByteArray(0)

        scope.launch { send(parameters) }
    }

    override suspend fun send(function: TdApi.Function<*>): TdApi.Object = suspendCoroutine { continuation ->
        client.send(function) { result ->
            when (result) {
                is TdApi.Error -> continuation.resumeWithException(RuntimeException("TDLib error: [code=${result.code}, message=${result.message}]"))
                else -> continuation.resume(result)
            }
        }
    }

    override suspend fun setPhoneNumber(phoneNumber: String) { send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null)) }
    override suspend fun checkCode(code: String) { send(TdApi.CheckAuthenticationCode(code)) }
    override suspend fun checkPassword(password: String) { send(TdApi.CheckAuthenticationPassword(password)) }
    override suspend fun logOut() { send(TdApi.LogOut()) }
    override suspend fun getMe(): TdApi.User = send(TdApi.GetMe()) as TdApi.User
    override suspend fun getUserFullInfo(userId: Long): TdApi.UserFullInfo = send(TdApi.GetUserFullInfo(userId)) as TdApi.UserFullInfo
    override suspend fun getChats(limit: Int): List<TdApi.Chat> {
        val chatList = TdApi.ChatListMain()
        send(TdApi.LoadChats(chatList, limit))
        val result = send(TdApi.GetChats(chatList, limit))
        return if (result is TdApi.Chats) result.chatIds.map { getChat(it) } else emptyList()
    }
    override suspend fun getChat(chatId: Long): TdApi.Chat = send(TdApi.GetChat(chatId)) as TdApi.Chat
    override suspend fun searchChats(query: String, limit: Int): List<TdApi.Chat> {
        val result = send(TdApi.SearchPublicChats(query)) as TdApi.Chats
        return result.chatIds.map { getChat(it) }
    }
    override suspend fun openChat(chatId: Long) { send(TdApi.OpenChat(chatId)) }
    override suspend fun closeChat(chatId: Long) { send(TdApi.CloseChat(chatId)) }
    override suspend fun getChatHistory(chatId: Long, fromMessageId: Long, offset: Int, limit: Int, onlyLocal: Boolean): Array<TdApi.Message> {
        val result = send(TdApi.GetChatHistory(chatId, fromMessageId, offset, limit, onlyLocal))
        return (result as TdApi.Messages).messages
    }
    override suspend fun getMessage(chatId: Long, messageId: Long): TdApi.Message = send(TdApi.GetMessage(chatId, messageId)) as TdApi.Message
    override suspend fun viewMessages(chatId: Long, messageIds: LongArray, forceRead: Boolean) {
        send(TdApi.ViewMessages(chatId, 0L, messageIds, null, forceRead))
    }
    override suspend fun sendTyping(chatId: Long) {
        send(TdApi.SendChatAction(chatId, 0, null, TdApi.ChatActionTyping()))
    }
    override suspend fun sendTextMessage(chatId: Long, text: String, replyTo: Long?): TdApi.Message {
        val content = TdApi.InputMessageText(TdApi.FormattedText(text, arrayOf()), null, true)
        return send(TdApi.SendMessage(chatId, 0, replyTo ?: 0L, null, content)) as TdApi.Message
    }
    override suspend fun editMessageText(chatId: Long, messageId: Long, text: TdApi.InputMessageText): TdApi.Message { return send(TdApi.EditMessageText(chatId, messageId, null, text)) as TdApi.Message }
    override suspend fun deleteMessages(chatId: Long, messageIds: LongArray, revoke: Boolean) { send(TdApi.DeleteMessages(chatId, messageIds, revoke)) }
    override suspend fun downloadFile(fileId: Int, priority: Int, synchronous: Boolean): TdApi.File {
        send(TdApi.DownloadFile(fileId, priority, 0, 0, synchronous))
        return getFile(fileId)
    }
    override suspend fun getFile(fileId: Int): TdApi.File = send(TdApi.GetFile(fileId)) as TdApi.File
}