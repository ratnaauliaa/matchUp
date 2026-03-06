package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit // Tambahkan parameter navigasi ke detail
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val historyList = viewModel.historyList

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (historyList.isNotEmpty()) {
                        TextButton(onClick = { showDeleteDialog = true }) {
                            Text("Clear", color = Color.Black, fontSize = 14.sp, fontFamily = MyCustomFontFamily)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color.White)) {
            if (historyList.isEmpty()) {
                // TAMPILAN EMPTY STATE
                EmptyHistoryContent()
            } else {
                // TAMPILAN DAFTAR HISTORY
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
                ) {
                    // Menggunakan itemsIndexed agar kita tahu nomor index data yang diklik
                    itemsIndexed(historyList) { index, historyItem ->
                        HistoryGroupSection(
                            date = historyItem.date,
                            details = historyItem.details,
                            onClick = { onNavigateToDetail(index) } // Klik ke detail
                        )
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
}

@Composable
fun HistoryGroupSection(date: String, details: List<String>, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp).clickable { onClick() }) {
        Text(
            text = date,
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp),
            fontFamily = MyCustomFontFamily
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp), tint = Color(0xFFD81B60))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your matches from:", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = MyCustomFontFamily)
                }

                Spacer(modifier = Modifier.height(8.dp))

                details.forEach { info ->
                    Text(info, fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "View more >",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily
                )
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
        // --- BAGIAN GAMBAR ---
        // Ganti R.drawable.history_empty dengan nama file gambarmu (misal: R.drawable.img_no_history)
        Image(
            painter = painterResource(id = R.drawable.history_empty),
            contentDescription = "No History",
            modifier = Modifier.size(180.dp) // Ukuran bisa kamu sesuaikan
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("No History Yet", fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = MyCustomFontFamily)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "No history saved yet.\nYour match history will show up here!",
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 40.dp),
            fontFamily = MyCustomFontFamily
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
                Text("Clear History?", fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = MyCustomFontFamily)
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
                        Text("Cancel", color = Color(0xFF5C6BC0), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onClearAll,
                        modifier = Modifier.weight(1f).height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear all", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}