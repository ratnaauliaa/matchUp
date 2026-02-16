package com.example.matchUp.fdmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.ui.theme.MyCustomFontFamily
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadeSelectionScreen(
    product: Product,
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    onAddAnother: () -> Unit,
    onFindMatches: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val selectedShade = viewModel.selectedShade
    val selectedMatches = viewModel.selectedMatches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- HEADER ---
        IconButton(
            onClick = {
                viewModel.selectedShade = null // Reset shade saat balik ke Step 2
                onBack()
            },
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Step 3",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MyCustomFontFamily
        )
        Text(
            text = "Finally, select what shade your wear in this product:",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- SELECT BOX ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (selectedShade != null) 2.dp else 1.dp,
                    color = if (selectedShade != null) Color.Black else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedShade?.shade_name ?: "Select",
                color = if (selectedShade != null) Color.Black else Color.Gray,
                fontSize = 15.sp,
                fontWeight = if (selectedShade != null) FontWeight.Bold else FontWeight.Normal
            )

            if (selectedShade != null && !isExpanded) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            val shadeToCancel = viewModel.selectedShade
                            val matchToRemove = viewModel.selectedMatches.find {
                                it.product == product && it.shade == shadeToCancel
                            }
                            if (matchToRemove != null) {
                                viewModel.removeMatch(viewModel.selectedMatches.indexOf(matchToRemove))
                            }
                            viewModel.selectedShade = null
                        },
                    tint = Color.Black
                )
            } else {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (selectedShade != null) Color.Black else Color.Gray
                )
            }
        }

        // --- LIST SHADE ---
        if (isExpanded) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(product.shades) { shade ->
                    val isThisSelected = selectedShade?.shade_name == shade.shade_name
                    Text(
                        text = shade.shade_name,
                        fontSize = 17.sp,
                        color = if (isThisSelected) Color.Black else Color(0xFF9E9E9E),
                        fontWeight = if (isThisSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedShade = shade
                                isExpanded = false
                            }
                            .padding(vertical = 14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // --- TOMBOL SUBMIT ---
        Button(
            onClick = {
                if (selectedShade != null) {
                    val isAlreadyInList = viewModel.selectedMatches.any { it.product == product && it.shade == selectedShade }
                    if (!isAlreadyInList) {
                        viewModel.addMatch(viewModel.selectedBrandName, product, selectedShade)
                    }
                    showSheet = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF9D7E4),
                disabledContainerColor = Color(0xFFF2F2F2)
            ),
            shape = RoundedCornerShape(30.dp),
            enabled = selectedShade != null
        ) {
            Text("Submit", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }

    // --- POP-UP BOTTOM SHEET ---
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "We'll calculate your matches based on you using :",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // List ringkasan
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    selectedMatches.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF9F9F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = item.product.image,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.brandName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black,
                                    fontFamily = MyCustomFontFamily
                                )

                                Text(
                                    text = "${item.product.product_name} - ${item.shade.shade_name}",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    fontFamily = MyCustomFontFamily,
                                    maxLines = 1
                                )

                                Text(
                                    text = "(remove)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4A90E2),
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .clickable {
                                            viewModel.removeMatch(index)
                                            if (viewModel.selectedMatches.isEmpty()) showSheet = false
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "*For more accurate matching, enter another foundation that you've used.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL NAVIGASI DI DALAM POP-UP ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showSheet = false

                                // --- RESET STATE SEBELUM TAMBAH PRODUK BARU ---
                                viewModel.selectedShade = null
                                viewModel.selectedProduct = null // Agar Step 2 kosong lagi

                                onAddAnother() // Pindah ke Step 1
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E9FF)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Add another match", color = Color.Black, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showSheet = false
                                onFindMatches()
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Find my matches", color = Color.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}