package com.example.matchUp.fdmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun ProductSelectionScreen(
    selectedBrandName: String,
    productListFromDb: List<Product>,
    onBack: () -> Unit,
    onNext: (Product) -> Unit
) {
    var showFullList by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // --- ERROR STATE: If no products found in database ---
    if (productListFromDb.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Database Unavailable",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "We're sorry, products for $selectedBrandName are not yet available in our database.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = MyCustomFontFamily
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go Back to Brand Selection", color = Color.White)
            }
        }
    } else {
        if (showFullList) {
            ProductListScreen(
                brandName = selectedBrandName,
                productList = productListFromDb,
                onProductSelected = { product ->
                    selectedProduct = product
                    showFullList = false
                },
                onClose = { showFullList = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // --- PROGRESS BAR & BACK BUTTON ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    SegmentedProgressBar(currentStep = 2, totalSteps = 3)

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
                    text = "Step 2",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily
                )

                Text(
                    text = "What product do you use from $selectedBrandName?",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // --- SEARCH BAR (CUSTOM BOX - SAME AS STEP 1) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = if (selectedProduct != null) Color.Black else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { showFullList = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = selectedProduct?.product_name ?: "Search product",
                            color = if (selectedProduct == null) Color.LightGray else Color.Black,
                            fontSize = 15.sp,
                            fontFamily = MyCustomFontFamily,
                            modifier = Modifier.weight(1f)
                        )

                        if (selectedProduct != null) {
                            IconButton(
                                onClick = { selectedProduct = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- FLOATING ACTION BUTTON ---
                if (selectedProduct != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            onClick = { onNext(selectedProduct!!) },
                            containerColor = Color(0xFFFFD1E3),
                            contentColor = Color.Black,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
                            modifier = Modifier
                                .padding(bottom = 30.dp, end = 20.dp)
                                .size(60.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}