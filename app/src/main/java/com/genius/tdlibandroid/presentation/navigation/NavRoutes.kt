package com.genius.tdlibandroid.presentation.navigation

object NavRoutes {
    // ⭐⭐ नेस्टेड नेविगेशन के लिए ग्राफ रूट्स जोड़े गए ⭐⭐
    const val LOGIN_GRAPH = "login_graph"
    const val MAIN_GRAPH = "main_graph"

    const val CHAT_LIST = "chat_list"
    const val CHAT = "chat/{chatId}"
    const val SETTINGS = "settings"
    const val PROFILE = "profile/{userId}"

    fun chat(chatId: Long): String {
        return "chat/$chatId"
    }

    fun profile(userId: Long): String {
        return "profile/$userId"
    }
}