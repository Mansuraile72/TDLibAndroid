package com.genius.tdlibandroid.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

// UI की स्थिति को दर्शाने के लिए डेटा क्लास
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userFullInfo: TdApi.UserFullInfo? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val client: TelegramClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // यूजर की पूरी जानकारी लोड करने के लिए फंक्शन
    fun loadUserProfile(userId: Long) {
        if (_uiState.value.userFullInfo != null) return // डेटा पहले से लोड है तो दोबारा न करें

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val fullInfo = client.getUserFullInfo(userId)
                _uiState.update {
                    it.copy(isLoading = false, userFullInfo = fullInfo)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
}