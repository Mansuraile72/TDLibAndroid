// पथ: app/src/main/java/com/genius/tdlibandroid/presentation/home/HomeScreen.kt
package com.genius.tdlibandroid.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.drinkless.tdlib.TdApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onChatClick: (Long) -> Unit // ⭐ सिग्नेचर बदला गया
) {
    val chats by viewModel.chats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chats") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (chats.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chats) { chat ->
                        ChatItem(
                            chat = chat,
                            // ⭐ क्लिक हैंडलर को पास किया गया
                            onItemClick = { onChatClick(chat.id) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chat: TdApi.Chat,
    onItemClick: () -> Unit // ⭐ सिग्नेचर बदला गया
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // ⭐ आइटम को क्लिक करने योग्य बनाया गया
            .clickable(onClick = onItemClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = chat.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        val lastMessageText = when (val content = chat.lastMessage?.content) {
            is TdApi.MessageText -> content.text.text
            is TdApi.MessagePhoto -> "📷 Photo"
            is TdApi.MessageVideo -> "📹 Video"
            is TdApi.MessageAudio -> "🎵 Audio"
            is TdApi.MessageDocument -> "📄 Document"
            else -> "Unsupported message type"
        }
        Text(
            text = lastMessageText,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}