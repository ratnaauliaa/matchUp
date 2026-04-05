package com.example.matchUp.fdmatch

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrandSelectionScreen(
    matchViewModel: MatchViewModel,
    brandListFromDb: List<BrandInfo>,
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    var showFullList by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf("") }

    if (showFullList) {
        BrandListScreen(
            brandListFromDbFull = matchViewModel.allBrandList,
            onBrandSelected = { brand ->
                selectedBrand = brand
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
                SegmentedProgressBar(currentStep = 1, totalSteps = 3)

                Spacer(modifier = Modifier.height(12.dp))

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, tint = Color.Black, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Step 1",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily
            )
            Text(
                text = "Enter a brand of foundation or concealer that you wear:",
                fontSize = 15.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- SEARCH BAR (METODE BOX: LEBIH STABIL & COMPACT) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = if (selectedBrand.isNotEmpty()) Color.Black else Color(0xFFE0E0E0),
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
                        text = if (selectedBrand.isEmpty()) "Search brand" else selectedBrand,
                        color = if (selectedBrand.isEmpty()) Color.LightGray else Color.Black,
                        fontSize = 15.sp,
                        fontFamily = MyCustomFontFamily,
                        modifier = Modifier.weight(1f)
                    )

                    if (selectedBrand.isNotEmpty()) {
                        IconButton(
                            onClick = { selectedBrand = "" },
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

            if (selectedBrand.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { onNext(selectedBrand) },
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
