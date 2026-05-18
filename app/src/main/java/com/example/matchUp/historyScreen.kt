package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun HistoryScreen(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    // TAMBAHKAN PARAMETER BARU: Callback khusus untuk mengarahkan rute detail Lips
    onNavigateToLipsDetail: (Int) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val historyList = viewModel.historyList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
            }

            Text(
                text = "History",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (historyList.isNotEmpty()) {
                TextButton(
                    onClick = { showDeleteDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Clear",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontFamily = MyCustomFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        // --- CONTENT AREA ---
        Box(modifier = Modifier.fillMaxSize()) {
            if (historyList.isEmpty()) {
                EmptyHistoryContent()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 30.dp)
                ) {
                    itemsIndexed(historyList) { index, historyItem ->
                        HistoryGroupSection(
                            date = historyItem.date,
                            details = historyItem.details,
                            onClick = {
                                // DIBAIKI: Deteksi kategori produk di dalam data input secara dinamis sebelum bernavigasi
                                val isLipsProduct = historyItem.inputProducts.any { matchData ->
                                    matchData.product.category.equals("lips", ignoreCase = true)
                                }

                                if (isLipsProduct) {
                                    onNavigateToLipsDetail(index)
                                } else {
                                    onNavigateToDetail(index)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        ClearHistoryDialog(
            onDismiss = { showDeleteDialog = false },
            onClearAll = {
                viewModel.clearAllHistory()
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun HistoryGroupSection(date: String, details: List<String>, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 10.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = date,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            fontFamily = MyCustomFontFamily
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFB800)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Match found from:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = MyCustomFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                details.forEach { info ->
                    Text(
                        text = "• $info",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        fontFamily = MyCustomFontFamily,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View details",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = MyCustomFontFamily
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.history_empty),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "No History Yet",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = MyCustomFontFamily
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "No history saved yet.\nYour match history will show up here!",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 40.dp),
            fontFamily = MyCustomFontFamily,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ClearHistoryDialog(onDismiss: () -> Unit, onClearAll: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Clear History?", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 20.sp, fontFamily = MyCustomFontFamily)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Are you sure you want to clear your match history?",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8EAF6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel",  fontFamily = MyCustomFontFamily, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onClearAll,
                        modifier = Modifier.weight(1f).height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear all", color = Color.Black,  fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}