// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/login/LoginScreen.kt
package com.genius.tdlibandroid.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.genius.tdlibandroid.data.AuthState // ⭐⭐⭐ यह इम्पोर्ट जोड़ा गया है ⭐⭐⭐

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val code by viewModel.code.collectAsState()
    val password by viewModel.password.collectAsState()

    if (authState is AuthState.Ready) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (authState) {
                is AuthState.WaitPhoneNumber, is AuthState.Uninitialized -> {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { viewModel.phoneNumber.value = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.sendPhoneNumber() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Send Phone Number")
                    }
                }
                is AuthState.WaitCode -> {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { viewModel.code.value = it },
                        label = { Text("Verification Code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.sendCode() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Send Code")
                    }
                }
                is AuthState.WaitPassword -> {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.password.value = it },
                        label = { Text("2FA Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.sendPassword() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Send Password")
                    }
                }
                is AuthState.WaitTdlibParameters, is AuthState.LoggingOut -> {
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Text("Error: ${(authState as AuthState.Error).message}")
                }
                else -> {
                    // Ready, Closed, Uninitialized के लिए कोई विशेष UI नहीं
                }
            }
        }
    }
}