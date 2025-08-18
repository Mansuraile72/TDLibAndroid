package com.genius.tdlibandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.genius.tdlibandroid.presentation.chat.ChatScreen
import com.genius.tdlibandroid.presentation.home.HomeScreen
import com.genius.tdlibandroid.presentation.home.HomeViewModel
// ⭐ नई प्रोफ़ाइल स्क्रीन को इम्पोर्ट करें
import com.genius.tdlibandroid.presentation.profile.ProfileScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.CHAT_LIST,
        modifier = modifier
    ) {
        composable(route = NavRoutes.CHAT_LIST) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onChatClick = { chatId ->
                    navController.navigate(NavRoutes.chat(chatId))
                }
            )
        }

        composable(
            route = NavRoutes.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.LongType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
            if (chatId > 0) {
                // ⭐ ChatScreen में navController पास करें
                ChatScreen(
                    chatId = chatId,
                    navController = navController
                )
            }
        }

        // ⭐⭐ नई प्रोफ़ाइल स्क्रीन का डेस्टिनेशन यहाँ जोड़ा गया है ⭐⭐
        composable(
            route = NavRoutes.PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            if (userId > 0) {
                ProfileScreen(userId = userId)
            }
        }

        composable(route = NavRoutes.SETTINGS) {
            // भविष्य में यहाँ SettingsScreen आएगी।
        }
    }
}