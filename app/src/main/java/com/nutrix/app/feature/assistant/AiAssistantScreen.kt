package com.nutrix.app.feature.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.GlassCard
import com.nutrix.app.core.designsystem.NutrixColors
import com.nutrix.app.core.engine.AiAssistantEngine
import com.nutrix.app.core.engine.ChatMessage
import com.nutrix.app.core.model.DietPlan
import com.nutrix.app.core.model.LoggedMeal
import com.nutrix.app.core.model.UserProfile

@Composable
fun AiAssistantScreen(
    userProfile: UserProfile,
    allMeals: List<LoggedMeal>,
    allDietPlans: List<DietPlan>,
    onBack: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello ! I am your NUTRIX Food Intelligence Assistant. Ask me anything about your logged history, weekly diet plan, or nutritious food alternatives.",
                isUser = false
            )
        )
    }

    val suggestionChips = listOf(
        "How often did I eat chocolate this week?",
        "Suggest a higher-protein breakfast",
        "What did I plan for tomorrow?",
        "Compare my plan with what I ate",
        "Is chicken curry healthy for my goal?"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NutrixColors.TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NutrixColors.VividViolet,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NUTRIX Assistant",
                        color = NutrixColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggestion Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestionChips) { chipText ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(NutrixColors.CardSurface)
                            .border(1.dp, NutrixColors.CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                messages.add(ChatMessage(text = chipText, isUser = true))
                                val reply = AiAssistantEngine.generateResponse(chipText, allMeals, allDietPlans, userProfile)
                                messages.add(ChatMessage(text = reply, isUser = false))
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = chipText,
                            color = NutrixColors.ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NutrixColors.CardSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about meals, calories, goals...", color = NutrixColors.TextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NutrixColors.TextPrimary,
                        unfocusedTextColor = NutrixColors.TextPrimary,
                        focusedBorderColor = NutrixColors.ElectricBlue,
                        unfocusedBorderColor = NutrixColors.CardBorder
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val trimmed = inputText.trim()
                        if (trimmed.isNotBlank()) {
                            messages.add(ChatMessage(text = trimmed, isUser = true))
                            inputText = ""
                            val reply = AiAssistantEngine.generateResponse(trimmed, allMeals, allDietPlans, userProfile)
                            messages.add(ChatMessage(text = reply, isUser = false))
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isUser) 18.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 18.dp
                    )
                )
                .background(if (message.isUser) NutrixColors.ElectricBlue else NutrixColors.CardSurface)
                .border(1.dp, if (message.isUser) NutrixColors.ElectricBlue else NutrixColors.CardBorder, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                color = NutrixColors.TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
