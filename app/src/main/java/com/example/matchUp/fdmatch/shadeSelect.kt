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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.ui.theme.MyCustomFontFamily
import kotlinx.coroutines.delay
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
    var currentStep by remember { mutableStateOf(2) }
    var isExpanded by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val selectedShade = viewModel.selectedShade
    val selectedMatches = viewModel.selectedMatches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // --- HEADER (Tetap) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            SegmentedProgressBar(currentStep = currentStep, totalSteps = 3)

            Spacer(modifier = Modifier.height(12.dp))

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color.Black,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Step 3",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = MyCustomFontFamily
        )
        Text(
            text = "Finally, select what shade your wear in this product:",
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = MyCustomFontFamily,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- SELECT BOX (Tetap) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedShade != null) Color.Black else Color(0xFFE0E0E0)
                )
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedShade?.shade_name ?: "Select",
                color = if (selectedShade != null) Color.Black else Color.Gray,
                fontSize = 15.sp,
                fontFamily = MyCustomFontFamily,
                fontWeight = if (selectedShade != null) FontWeight.Normal else FontWeight.Normal
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

        // --- LIST SHADE (DIBATASI TINGGINYA) ---
        if (isExpanded) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp) // Maksimal sekitar 5 item
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(product.shades) { shade ->
                    val isThisSelected = selectedShade?.shade_name == shade.shade_name
                    Text(
                        text = shade.shade_name,
                        fontSize = 15.sp,
                        color = Color.Black,
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
        }

        // --- SPACER & TOMBOL SUBMIT ---
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp), // Beri jarak bawah agar tidak kena navigasi bar
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    if (selectedShade != null) {
                        currentStep = 3
                        val isAlreadyInList = viewModel.selectedMatches.any { it.product == product && it.shade == selectedShade }
                        if (!isAlreadyInList) {
                            viewModel.addMatch(viewModel.selectedBrandName, product, selectedShade)
                        }
                        showSheet = true
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD1E3),
                    disabledContainerColor = Color(0xFFF2F2F2)
                ),
                shape = RoundedCornerShape(20.dp),
                enabled = selectedShade != null
            ) {
                Text(
                    text = "Submit",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily
                )
            }
        }
    }


    // --- POP-UP BOTTOM SHEET (Tetap) ---
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
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                                    .size(45.dp)
                                    .background(Color(0xFFF9F9F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = item.product.image,
                                    contentDescription = null,
                                    modifier = Modifier.size(45.dp),
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
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showSheet = false
                                viewModel.selectedShade = null
                                viewModel.selectedProduct = null
                                onAddAnother()
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E9FF)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Add another match",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontFamily = MyCustomFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            if (!isLoading) {
                                isLoading = true
                                scope.launch {
                                    // 1. Sembunyikan Sheet
                                    sheetState.hide()


                                    // 3. Jalankan fungsi cari match
                                    onFindMatches()

                                    // 4. Tutup sheet di UI state
                                    showSheet = false

                                    // 5. Reset loading (opsional, karena biasanya sudah pindah halaman)
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f) // Lebar seragam dengan tombol "Add another"
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD1E3),
                            disabledContainerColor = Color(0xFFFFD1E3).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isLoading // Matikan tombol saat loading
                    ) {
                        // Box memastikan konten tetap di tengah meskipun ada perubahan state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Find my matches",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    fontFamily = MyCustomFontFamily,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
