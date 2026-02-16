package com.example.matchUp.fdmatch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BrandListScreen(
    brandListFromDbFull: List<BrandInfo>, // Menggunakan list objek lengkap
    onBrandSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Filter berdasarkan teks pencarian
    val filteredBrands = brandListFromDbFull
        .filter { it.name.contains(searchText, ignoreCase = true) }
        .sortedBy { it.name }

    // Mengelompokkan berdasarkan huruf depan untuk Sticky Header
    val grouped = filteredBrands.groupBy { it.name.first().uppercaseChar() }
    val alphabetIndex = grouped.keys.toList().sorted()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- Search Bar Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 20.dp, start = 8.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
            }

            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = MyCustomFontFamily
                ),
                decorationBox = { innerTextField ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (searchText.isNotEmpty()) Color.Black else Color(0xFFDEDEDE)
                        ),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Enter the brand",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontFamily = MyCustomFontFamily
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
        }

        // --- List Content Area ---
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                grouped.forEach { (letter, brands) ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF7F7F7))
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    items(brands) { brandItem ->
                        val isActive = brandItem.status == "active"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (isActive) 1f else 0.5f) // Buram jika inactive
                                .clickable(enabled = isActive) {
                                    onBrandSelected(brandItem.name)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = brandItem.name,
                                    fontSize = 14.sp,
                                    fontFamily = MyCustomFontFamily,
                                    color = if (isActive) Color.Black else Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )

                                if (!isActive) {
                                    Text(
                                        text = "Coming Soon",
                                        fontSize = 10.sp,
                                        color = Color.LightGray,
                                        fontFamily = MyCustomFontFamily
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color(0xFFF0F0F0)
                            )
                        }
                    }
                }
            }

            // --- ALPHABET SHORTCUT ---
            if (searchText.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    alphabetIndex.forEach { char ->
                        Text(
                            text = char.toString(),
                            modifier = Modifier
                                .clickable {
                                    scope.launch {
                                        val index = calculateScrollIndex(grouped, char)
                                        listState.animateScrollToItem(index)
                                    }
                                }
                                .padding(vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

fun calculateScrollIndex(grouped: Map<Char, List<BrandInfo>>, target: Char): Int {
    var count = 0
    for ((char, list) in grouped.toSortedMap()) {
        if (char == target) return count
        count += 1 + list.size
    }
    return count
}