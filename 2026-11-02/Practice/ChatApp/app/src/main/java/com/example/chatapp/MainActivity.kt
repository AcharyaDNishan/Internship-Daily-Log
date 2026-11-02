package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private var currentUserProfile: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppMain(
                        onUserChanged = { user ->
                            currentUserProfile = user
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentUserProfile?.let { user ->
            FirebaseManager.deleteUserDataByUid(user.uid)
        }
    }
}

sealed class Screen {
    object UserRegistration : Screen()
    object UserList : Screen()
    data class Chat(val chatroom: Chatroom) : Screen()
}

@Composable
fun ChatAppMain(onUserChanged: (User?) -> Unit = {}) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.UserRegistration) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current

    LaunchedEffect(currentUser) {
        onUserChanged(currentUser)
        currentUser?.let { user ->
            val serviceIntent = Intent(context, CleanupService::class.java).apply {
                putExtra("user_uid", user.uid)
            }
            context.startService(serviceIntent)
        }
    }

    when (val screen = currentScreen) {
        is Screen.UserRegistration -> {
            UserRegistrationScreen(
                onUserCreated = { user ->
                    currentUser = user
                    currentScreen = Screen.UserList
                }
            )
        }
        is Screen.UserList -> {
            val user = currentUser
            if (user != null) {
                UserListScreen(
                    currentUser = user,
                    onChatroomSelected = { chatroom ->
                        currentScreen = Screen.Chat(chatroom)
                    }
                )
            }
        }
        is Screen.Chat -> {
            val user = currentUser
            if (user != null) {
                ChatScreen(
                    currentUser = user,
                    chatroom = screen.chatroom,
                    onBack = { currentScreen = Screen.UserList }
                )
            }
        }
    }
}

@Composable
fun UserRegistrationScreen(onUserCreated: (User) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Chat App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Enter Nickname") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nickname.isNotBlank()) {
                    isLoading = true
                    FirebaseManager.createUser(nickname.trim()) { user ->
                        isLoading = false
                        if (user != null) {
                            onUserCreated(user)
                        }
                    }
                }
            },
            enabled = nickname.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Generate Random UID & Register")
            }
        }
    }
}

@Composable
fun UserListScreen(
    currentUser: User,
    onChatroomSelected: (Chatroom) -> Unit
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        FirebaseManager.getUsers { fetched ->
            users = fetched.filter { it.uid != currentUser.uid }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Log In Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text("Nickname: ${currentUser.nickname}")
                Text("UID: ${currentUser.uid}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Text(
            text = "Select a user to start chatroom",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No other users registered yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { targetUser ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                FirebaseManager.getOrCreateChatroom(currentUser, targetUser) { chatroom ->
                                    if (chatroom != null) {
                                        onChatroomSelected(chatroom)
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = targetUser.nickname,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "UID: ${targetUser.uid}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text("Chat ->", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    currentUser: User,
    chatroom: Chatroom,
    onBack: () -> Unit
) {
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputMessage by remember { mutableStateOf("") }

    DisposableEffect(chatroom.chatroomId) {
        val listener = FirebaseManager.listenToMessages(chatroom.chatroomId) { updated ->
            messages = updated
        }
        onDispose {
            listener.remove()
        }
    }

    val isCurrentUser1 = currentUser.uid == chatroom.user1Uid
    val partnerNickname = if (isCurrentUser1) chatroom.user2Nickname else chatroom.user1Nickname

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Header
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack) {
                    Text("< Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Chat with $partnerNickname",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Chatroom ID: ${chatroom.chatroomId}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Messages Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.userId == currentUser.uid || msg.senderNickname == currentUser.nickname
                val displayMsg = msg.msg

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Text(
                        text = msg.senderNickname,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = displayMsg,
                            color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // Message Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                label = { Text("Type a message") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val text = inputMessage.trim()
                    if (text.isNotEmpty()) {
                        FirebaseManager.sendMessage(chatroom, currentUser, text)
                        inputMessage = ""
                    }
                },
                enabled = inputMessage.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}