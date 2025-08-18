package com.genius.tdlibandroid.data

sealed class AuthState {
    data object Uninitialized       : AuthState()
    data object WaitTdlibParameters : AuthState()
    data object WaitPhoneNumber     : AuthState()
    data object WaitCode            : AuthState()
    data object WaitPassword        : AuthState()
    data object Ready               : AuthState()
    data object LoggingOut          : AuthState()
    data object Closed              : AuthState()
    data class Error(val message: String) : AuthState()
}
