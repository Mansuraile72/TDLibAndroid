package com.genius.tdlibandroid.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.genius.tdlibandroid.data.AuthState
import com.genius.tdlibandroid.data.TelegramClient
import com.genius.tdlibandroid.presentation.login.LoginScreen
import com.genius.tdlibandroid.presentation.navigation.AppNavGraph
import com.genius.tdlibandroid.presentation.navigation.NavRoutes
import com.genius.tdlibandroid.ui.theme.TDLibAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HiddenTelegramActivity : ComponentActivity() {

    @Inject
    lateinit var telegramClient: TelegramClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TDLibAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by telegramClient.authState.collectAsState()
                    App(authState = authState)
                }
            }
        }
    }
}

@Composable
fun App(authState: AuthState) {
    val navController = rememberNavController()

    // जब उपयोगकर्ता लॉगिन हो जाए, तो सीधे मुख्य ऐप पर जाएं
    LaunchedEffect(authState) {
        if (authState is AuthState.Ready) {
            navController.navigate(NavRoutes.MAIN_GRAPH) {
                // ⭐ popUpTo ब्लॉक को सही सिंटैक्स के साथ ठीक किया गया
                popUpTo(NavRoutes.LOGIN_GRAPH) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = NavRoutes.LOGIN_GRAPH) {
        composable(route = NavRoutes.LOGIN_GRAPH) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    // LaunchedEffect अब नेविगेशन को संभालता है
                }
            )
        }

        composable(route = NavRoutes.MAIN_GRAPH) {
            // AppNavGraph अब अपने स्वयं के नेविगेशन को मैनेज करेगा
            AppNavGraph(navController = rememberNavController())
        }
    }
}