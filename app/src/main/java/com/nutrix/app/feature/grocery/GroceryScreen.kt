package com.nutrix.app.feature.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrix.app.core.designsystem.*
import com.nutrix.app.core.model.GroceryItem

@Composable
fun GroceryScreen(
    groceries: List<GroceryItem>,
    onTogglePurchased: (GroceryItem) -> Unit,
    onAddItem: (GroceryItem) -> Unit,
    onClearPurchased: () -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NutrixColors.BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Grocery List",
                        color = NutrixColors.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NutrixColors.ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle & Clear Purchased button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredients for this week\'s plan",
                    color = NutrixColors.TextSecondary,
                    fontSize = 13.sp
                )
                TextButton(onClick = onClearPurchased) {
                    Text("Clear Done", color = NutrixColors.ElectricBlue, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items List
            if (groceries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Grocery list is empty. Add items or generate from diet plan!",
                        color = NutrixColors.TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(groceries) { item ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp),
                            onClick = { onTogglePurchased(item) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (item.isPurchased) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (item.isPurchased) NutrixColors.MintGreen else NutrixColors.TextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            color = if (item.isPurchased) NutrixColors.TextMuted else NutrixColors.TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        Text(
                                            text = item.category,
                                            color = NutrixColors.TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = item.quantity,
                                    color = if (item.isPurchased) NutrixColors.TextMuted else NutrixColors.ElectricBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Item Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = NutrixColors.CardSurface,
                title = { Text("Add Grocery Item", color = NutrixColors.TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            placeholder = { Text("Item name (e.g. Tomatoes)", color = NutrixColors.TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NutrixColors.TextPrimary,
                                unfocusedTextColor = NutrixColors.TextPrimary,
                                focusedBorderColor = NutrixColors.ElectricBlue,
                                unfocusedBorderColor = NutrixColors.CardBorder
                            )
                        )
                        OutlinedTextField(
                            value = itemQty,
                            onValueChange = { itemQty = it },
                            placeholder = { Text("Quantity (e.g. 1 kg)", color = NutrixColors.TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NutrixColors.TextPrimary,
                                unfocusedTextColor = NutrixColors.TextPrimary,
                                focusedBorderColor = NutrixColors.ElectricBlue,
                                unfocusedBorderColor = NutrixColors.CardBorder
                            )
                        )
                    }
                },
                confirmButton = {
                    NutrixButton(
                        text = "Add",
                        onClick = {
                            if (itemName.isNotBlank()) {
                                onAddItem(
                                    GroceryItem(
                                        name = itemName.trim(),
                                        quantity = itemQty.ifBlank { "1 unit" },
                                        category = "Pantry"
                                    )
                                )
                                itemName = ""
                                itemQty = ""
                                showAddDialog = false
                            }
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = NutrixColors.TextSecondary)
                    }
                }
            )
        }
    }
}
