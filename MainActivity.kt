package com.nebula.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF0B0B12)
private val Panel = Color(0xFF151522)
private val Purple = Color(0xFF9B6DFF)
private val Muted = Color(0xFF9A98AD)
private val White = Color(0xFFF6F4FF)

private data class Chat(val name: String, val message: String, val time: String, val unread: Int, val emoji: String, val online: Boolean)
private val sampleChats = listOf(
    Chat("Alex", "Голосовое сообщение", "20:41", 2, "A", true),
    Chat("NEBULA Team", "Добро пожаловать в NEBULA!", "19:58", 5, "✦", true),
    Chat("Мария", "Окей, договорились 👌", "18:22", 0, "М", false),
    Chat("Илья", "Скинул фотографии", "Вчера", 0, "И", false),
    Chat("Космический чат", "Новый участник присоединился", "Вчера", 0, "☄", false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NebulaApp() }
    }
}

@Composable
private fun NebulaApp() {
    var tab by remember { mutableStateOf(0) }
    var selectedChat by remember { mutableStateOf<Chat?>(null) }
    var coins by remember { mutableIntStateOf(100) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Purple, background = Night, surface = Panel,
            onBackground = White, onSurface = White
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Night) {
            if (selectedChat != null) {
                ChatScreen(selectedChat!!, onBack = { selectedChat = null })
            } else {
                Column(Modifier.fillMaxSize()) {
                    when (tab) {
                        0 -> ChatsScreen(onOpen = { selectedChat = it })
                        1 -> WalletScreen(coins = coins, onSend = { amount ->
                            if (amount in 1..coins) coins -= amount
                        })
                        else -> ProfileScreen(coins)
                    }
                    NavigationBar(containerColor = Panel, contentColor = White) {
                        NavigationBarItem(
                            selected = tab == 0, onClick = { tab = 0 },
                            icon = { Icon(Icons.Default.ChatBubble, null) }, label = { Text("Чаты") },
                            colors = navColors()
                        )
                        NavigationBarItem(
                            selected = tab == 1, onClick = { tab = 1 },
                            icon = { Icon(Icons.Default.AccountBalanceWallet, null) }, label = { Text("NBC") },
                            colors = navColors()
                        )
                        NavigationBarItem(
                            selected = tab == 2, onClick = { tab = 2 },
                            icon = { Icon(Icons.Default.Person, null) }, label = { Text("Профиль") },
                            colors = navColors()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Purple, selectedTextColor = Purple,
    indicatorColor = Color(0xFF302347), unselectedIconColor = Muted, unselectedTextColor = Muted
)

@Composable
private fun BrandHeader(subtitle: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(46.dp).clip(RoundedCornerShape(15.dp))
                .background(Brush.linearGradient(listOf(Purple, Color(0xFF5B38B5)))),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.AutoAwesome, null, tint = White, modifier = Modifier.size(27.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("NEBULA", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = White)
            Text(subtitle, fontSize = 12.sp, color = Muted)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.Search, "Поиск", tint = White) }
        IconButton(onClick = {}) { Icon(Icons.Default.Edit, "Новое сообщение", tint = Purple) }
    }
}

@Composable
private fun ChatsScreen(onOpen: (Chat) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        BrandHeader("ТВОЁ ПРОСТРАНСТВО ДЛЯ ОБЩЕНИЯ")
        Row(Modifier.padding(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text("Все") }, colors = chipColors())
            FilterChip(selected = false, onClick = {}, label = { Text("Личные") }, colors = chipColors())
            FilterChip(selected = false, onClick = {}, label = { Text("Группы") }, colors = chipColors())
        }
        Text("СООБЩЕНИЯ", Modifier.padding(start = 22.dp, top = 18.dp, bottom = 8.dp), color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
        LazyColumn(contentPadding = PaddingValues(bottom = 14.dp)) {
            items(sampleChats) { chat ->
                Row(
                    Modifier.fillMaxWidth().clickable { onOpen(chat) }.padding(horizontal = 18.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(54.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF6845A5), Color(0xFF25233B)))), contentAlignment = Alignment.Center) {
                        Text(chat.emoji, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = White)
                        if (chat.online) Box(Modifier.size(12.dp).align(Alignment.BottomEnd).clip(CircleShape).background(Color(0xFF54D99A)).padding(2.dp).background(Panel, CircleShape))
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(chat.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = White)
                        Spacer(Modifier.height(4.dp))
                        Text(chat.message, color = Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(chat.time, color = Muted, fontSize = 11.sp)
                        if (chat.unread > 0) {
                            Spacer(Modifier.height(6.dp))
                            Box(Modifier.clip(CircleShape).background(Purple).padding(horizontal = 7.dp, vertical = 3.dp)) {
                                Text(chat.unread.toString(), color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFF222231), modifier = Modifier.padding(start = 85.dp, end = 18.dp))
            }
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Color(0xFF302347), selectedLabelColor = Purple,
    containerColor = Panel, labelColor = Muted
)

@Composable
private fun ChatScreen(chat: Chat, onBack: () -> Unit) {
    var draft by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf("Привет! Добро пожаловать в NEBULA ✦", "Это демонстрационный чат. Сообщения пока не отправляются на сервер.")) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Panel).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад", tint = White) }
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF48316E)), contentAlignment = Alignment.Center) { Text(chat.emoji, color = White) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(chat.name, color = White, fontWeight = FontWeight.Bold)
                Text(if (chat.online) "в сети" else "был(а) недавно", color = if (chat.online) Color(0xFF54D99A) else Muted, fontSize = 12.sp)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = White) }
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.Bottom) {
            messages.forEachIndexed { i, message ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (i % 2 == 0) Arrangement.Start else Arrangement.End) {
                    Surface(color = if (i % 2 == 0) Panel else Color(0xFF34234D), shape = RoundedCornerShape(18.dp)) {
                        Text(message, Modifier.widthIn(max = 290.dp).padding(13.dp), color = White, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(9.dp))
            }
        }
        Row(Modifier.fillMaxWidth().background(Panel).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Default.Add, "Вложение", tint = Muted) }
            OutlinedTextField(
                value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f),
                placeholder = { Text("Сообщение…", color = Muted) }, singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple, unfocusedBorderColor = Color(0xFF393447),
                    focusedTextColor = White, unfocusedTextColor = White, cursorColor = Purple
                )
            )
            IconButton(onClick = {
                if (draft.isNotBlank()) {
                    messages = messages + draft.trim()
                    draft = ""
                }
            }) { Icon(Icons.Default.Send, "Отправить (демо)", tint = Purple) }
        }
    }
}

@Composable
private fun WalletScreen(coins: Int, onSend: (Int) -> Unit) {
    var amount by remember { mutableStateOf("10") }
    var recipient by remember { mutableStateOf("") }
    var notice by remember { mutableStateOf("Это демонстрационный баланс на устройстве.") }
    Column(Modifier.fillMaxSize()) {
        BrandHeader("ВНУТРЕННЯЯ ВАЛЮТА")
        Column(Modifier.padding(horizontal = 20.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color.Transparent
            ) {
                Column(Modifier.background(Brush.linearGradient(listOf(Color(0xFF5D3D98), Color(0xFF241B3A)))).padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Token, null, tint = Color(0xFFD7C3FF), modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("NEBULA COIN", color = Color(0xFFD7C3FF), letterSpacing = 1.5.sp, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("$coins NBC", fontSize = 36.sp, fontWeight = FontWeight.Black, color = White)
                    Text("Доступный баланс", color = Color(0xFFC7B8E2), fontSize = 13.sp)
                    Spacer(Modifier.height(20.dp))
                    Text("✦  Виртуальные монеты для экосистемы NEBULA", color = White, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("Перевести монеты", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = recipient, onValueChange = { recipient = it }, modifier = Modifier.fillMaxWidth(), label = { Text("ID получателя") }, singleLine = true)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = amount, onValueChange = { amount = it.filter(Char::isDigit).take(8) }, modifier = Modifier.fillMaxWidth(), label = { Text("Количество NBC") }, singleLine = true)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {
                    val n = amount.toIntOrNull() ?: 0
                    if (recipient.isBlank()) notice = "Укажи ID получателя."
                    else if (n !in 1..coins) notice = "Недостаточно монет или неверная сумма."
                    else { onSend(n); notice = "Демо: $n NBC списано локально. Реальный перевод ещё не подключён." }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(15.dp)
            ) { Icon(Icons.Default.Send, null); Spacer(Modifier.width(8.dp)); Text("Перевести") }
            Spacer(Modifier.height(12.dp))
            Text(notice, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))
            Surface(color = Panel, shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = Purple)
                    Spacer(Modifier.width(10.dp))
                    Text("NBC — игровая валюта прототипа. Она не имеет денежной стоимости.", color = Muted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(coins: Int) {
    Column(Modifier.fillMaxSize()) {
        BrandHeader("ТВОЙ АККАУНТ")
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(100.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Purple, Color(0xFF3B2860)))), contentAlignment = Alignment.Center) {
                Text("N", fontSize = 44.sp, fontWeight = FontWeight.Black, color = White)
            }
            Spacer(Modifier.height(14.dp))
            Text("Nebula User", color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text("@nebula_user • демо-профиль", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(25.dp))
            ProfileRow(Icons.Default.Badge, "Уникальный ID", "NEB-10428")
            ProfileRow(Icons.Default.AccountBalanceWallet, "Баланс", "$coins NBC")
            ProfileRow(Icons.Default.Security, "Безопасность", "Демо-режим")
            ProfileRow(Icons.Default.Settings, "Настройки", "Скоро")
            Spacer(Modifier.height(20.dp))
            Text("NEBULA v0.1.0", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
        }
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp).clip(RoundedCornerShape(14.dp)).background(Panel).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Purple)
        Spacer(Modifier.width(13.dp))
        Text(title, Modifier.weight(1f), color = White, fontSize = 14.sp)
        Text(value, color = Muted, fontSize = 12.sp)
    }
}
