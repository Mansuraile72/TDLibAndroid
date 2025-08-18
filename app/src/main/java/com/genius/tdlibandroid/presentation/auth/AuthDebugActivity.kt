// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/auth/AuthDebugActivity.kt
package com.genius.tdlibandroid.presentation.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

/**
 * Debug screen (temporary stub).
 * Old XML/ViewBinding-based UI removed to unblock the build.
 * Replace with real Compose UI later if needed.
 */
class AuthDebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Text(text = "AuthDebugActivity (stub) — build unblocked ✅")
                }
            }
        }
    }
}