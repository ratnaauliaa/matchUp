package com.example.matchUp.fdmatch

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbDownOffAlt
import androidx.compose.material.icons.outlined.ThumbUpOffAlt
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    productName: String,
    initialShadeName: String? = null,
    viewModel: MatchViewModel,
    onBack: () -> Unit
) {
    // 1. Tambahkan pengecekan data di awal (Safety First)
    val brandDetail = viewModel.productsData.find { brand ->
        brand.products.any { it.product_name == productName }
    }
    val productData = brandDetail?.products?.find { it.product_name == productName }

    val lastMatch = viewModel.selectedMatches.lastOrNull()

    // Perbaikan: Tambahkan default hex jika lastMatch null agar tidak error saat hitung distance
    val bestShade = productData?.shades?.minByOrNull { shade ->
        viewModel.calculateColorDistance(lastMatch?.shade?.hex ?: "#FFFFFF", shade.hex)
    }

    // Ganti state ini
    var selectedShadeName by remember { mutableStateOf(initialShadeName ?: "") }

    // Update logika LaunchedEffect agar lebih pintar
    LaunchedEffect(productData, bestShade) {
        if (selectedShadeName.isEmpty()) {
            // Jika tidak ada initialShadeName, baru gunakan hasil hitungan bestShade
            selectedShadeName = bestShade?.shade_name ?: ""
        }
    }

    // Cari data shade yang sedang dipilih (untuk ditampilkan di Undertone/Skintone detail)
    val currentDisplayShade = productData?.shades?.find { it.shade_name == selectedShadeName } ?: bestShade
    var feedback by remember { mutableStateOf<Boolean?>(null) }

    // Perbaikan: Gunakan derivedStateOf agar UI responsif saat klik "Love"
    val isSaved by remember(viewModel.savedProducts.size) {
        derivedStateOf { viewModel.savedProducts.any { it.product_name == productName } }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- TOP BAR ---
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).border(BorderStroke(1.dp, Color(0xFFE0E0E0)), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color.Black,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Product Details",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp, // Sesuaikan ukuran agar tidak terlalu besar
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold
            )
            val isSaved = viewModel.isProductSaved(productData?.product_name ?: "")

            IconButton(
                onClick = {
                    productData?.let {
                        // FIX: Tambahkan brandDetail?.brand sebagai parameter kedua
                        viewModel.toggleSaveProduct(it, brandDetail?.brand ?: "Unknown Brand")
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isSaved) Color.Red else Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            // 2. GAMBAR PRODUK
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp).padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = productData?.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. NAMA PRODUK
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = brandDetail?.brand ?: "Unknown Brand",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = productData?.product_name ?: "Unknown Product",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(10.dp))

            // 3. DETAIL MATCH
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Surface(
                    color = Color(0xFFF9F9F9),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Perfect Match is:",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = MyCustomFontFamily
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bestShade?.shade_name ?: "Calculating...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = MyCustomFontFamily
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                        // Ganti bagian Row di dalam Surface Detail Match:
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gunakan currentDisplayShade agar datanya dinamis
                            DetailItem(label = "Undertone", value = currentDisplayShade?.undertone ?: "-")
                            Spacer(modifier = Modifier.width(60.dp))
                            DetailItem(label = "Skintone", value = currentDisplayShade?.skintone ?: "-")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Available Shades", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
                Text("Selected: $selectedShadeName", fontSize = 13.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)
                Spacer(modifier = Modifier.height(12.dp))

                // PERBAIKAN: Gunakan try-catch pada Color.parseColor agar tidak crash jika HEX salah
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    productData?.shades?.forEach { shade ->
                        val isRecommended = shade.shade_name == bestShade?.shade_name
                        val isCurrentSelection = shade.shade_name == selectedShadeName

                        val shadeColor = try {
                            Color(android.graphics.Color.parseColor(shade.hex))
                        } catch (e: Exception) {
                            Color.LightGray
                        }

                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp).clip(CircleShape)
                                    .background(shadeColor)
                                    .border(if (isCurrentSelection) 1.dp else 0.dp, Color.Black, CircleShape)
                                    .clickable { selectedShadeName = shade.shade_name }
                            )
                            if (isRecommended) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                // PERBAIKAN: Cek null pada description agar String.length() tidak error
                if (!productData?.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Description", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = productData?.description ?: "",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Justify,
                        fontFamily = MyCustomFontFamily,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                FeedbackSection(currentFeedback = feedback, onFeedbackClick = { feedback = it })
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontFamily = MyCustomFontFamily
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = MyCustomFontFamily
        )
    }
}

@Composable
fun FeedbackSection(currentFeedback: Boolean?, onFeedbackClick: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(250.dp)
                .height(45.dp),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Did this shade match you?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    fontFamily = MyCustomFontFamily,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onFeedbackClick(true) },
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            imageVector = if (currentFeedback == true) Icons.Default.ThumbUp else Icons.Outlined.ThumbUpOffAlt,
                            contentDescription = null,
                            tint = if (currentFeedback == true) Color(0xFF4CAF50) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = { onFeedbackClick(false) },
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            imageVector = if (currentFeedback == false) Icons.Default.ThumbDown else Icons.Outlined.ThumbDownOffAlt,
                            contentDescription = null,
                            tint = if (currentFeedback == false) Color.Red else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}