// पथ: app/src/main/java/com/genius/tdlibandroid/core/TgBaseViewModel.kt
package com.genius.tdlibandroid.core

import androidx.lifecycle.ViewModel
import com.genius.tdlibandroid.data.AuthState
import com.genius.tdlibandroid.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

/**
 * Base VM that exposes Telegram auth state.
 * Old direct-client callbacks removed (no raw `client`, no `it` usages).
 */
@HiltViewModel
open class TgBaseViewModel @Inject constructor(
    protected val telegramClient: TelegramClient
) : ViewModel() {

    /** Observe this from UI/child-VMs */
    val authState: StateFlow<AuthState> = telegramClient.authState

    /** Backward-compatible helper if older code calls getAuthTts() */
    fun getAuthStatus(): StateFlow<AuthState> = authState
}