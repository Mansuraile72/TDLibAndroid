package com.genius.tdlibandroid.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    userId: Long,
    // ⭐ ViewModel को जोड़ा गया
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // ⭐ जब स्क्रीन पहली बार बने तो यूजर प्रोफाइल लोड करें
    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    // ⭐ ViewModel से UI की स्थिति प्राप्त करें
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ⭐ लोडिंग की स्थिति को हैंडल करें
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        // ⭐ एरर की स्थिति को हैंडल करें
        uiState.error?.let { error ->
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }

        // ⭐ डेटा सफलतापूर्वक लोड होने पर UI दिखाएं
        uiState.userFullInfo?.let { userFullInfo ->
            val user = userFullInfo.user
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "@${user.username.ifEmpty { "N/A" }}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.phoneNumber,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userFullInfo.bio?.text ?: "No bio available.",
                    fontSize = 18.sp
                )
            }
        }
    }
}