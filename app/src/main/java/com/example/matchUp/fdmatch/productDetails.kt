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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.ui.theme.MyCustomFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    productName: String,
    viewModel: MatchViewModel,
    onBack: () -> Unit
) {
    // 1. MENGAMBIL DATA PRODUK DARI DATABASE JSON
    // Perbaikan: Cari BrandDetail dulu untuk mendapatkan nama brand-nya
    val brandDetail = viewModel.productsData.find { brandDetail ->
        brandDetail.products.any { it.product_name == productName }
    }
    val productData = brandDetail?.products?.find { it.product_name == productName }

    // 2. MENCARI SHADE TERBAIK
    val lastMatch = viewModel.selectedMatches.lastOrNull()
    val bestShade = productData?.shades?.minByOrNull { shade ->
        viewModel.calculateColorDistance(
            lastMatch?.shade?.hex ?: "#FFFFFF",
            shade.hex
        )
    }

    var selectedShadeName by remember { mutableStateOf("") }

    LaunchedEffect(bestShade) {
        if (selectedShadeName.isEmpty()) {
            selectedShadeName = bestShade?.shade_name ?: ""
        }
    }

    var feedback by remember { mutableStateOf<Boolean?>(null) }
    val isSaved = viewModel.savedProducts.any { it.product_name == productName }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        productData?.let { data ->

                            val newItem = Product(
                                id = data.product_name.hashCode(),
                                brand = brandDetail?.brand ?: "",
                                product_name = data.product_name,
                                image = data.image,
                                shades = data.shades,
                                description = data.description ?: ""
                            )
                            viewModel.toggleSaveProduct(newItem)
                        }
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isSaved) Color.Red else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (productData == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Product not found", fontFamily = MyCustomFontFamily)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                // SECTION: HEADER & IMAGE
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tampilkan Nama Brand di atas Nama Produk
                    Text(
                        text = brandDetail?.brand?.uppercase() ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontFamily = MyCustomFontFamily
                    )
                    Text(
                        text = productData.product_name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003366),
                        fontFamily = MyCustomFontFamily,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AsyncImage(
                        model = productData.image,
                        contentDescription = null,
                        modifier = Modifier.size(220.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Divider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                // SECTION: REKOMENDASI SHADE
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Your Perfect Match is ${bestShade?.shade_name ?: "-"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontFamily = MyCustomFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail Row sekarang aman karena Shade sudah punya properti ini
                    DetailRow(
                        label = "Undertone",
                        value = bestShade?.undertone ?: "Neutral",
                        color = Color(0xFF8BC34A)
                    )
                    DetailRow(
                        label = "Skintone",
                        value = bestShade?.skintone ?: "Medium",
                        color = Color(0xFFD2B48C)
                    )
                }

                Divider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                // SECTION: AVAILABLE SHADES
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Available in :", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = MyCustomFontFamily)

                    Text(
                        text = buildAnnotatedString {
                            append("Color: $selectedShadeName")
                            if (selectedShadeName == bestShade?.shade_name) {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))) {
                                    append(" → best match for you!")
                                }
                            }
                        },
                        fontSize = 13.sp,
                        fontFamily = MyCustomFontFamily
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        productData.shades.forEach { shade ->
                            val isRecommended = shade.shade_name == bestShade?.shade_name
                            val isCurrentSelection = shade.shade_name == selectedShadeName

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(shade.hex)))
                                    .border(
                                        width = if (isCurrentSelection) 2.dp else if (isRecommended) 1.dp else 0.dp,
                                        color = if (isCurrentSelection) Color.Black else if (isRecommended) Color.LightGray else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedShadeName = shade.shade_name }
                            )
                        }
                    }

                    // Deskripsi Produk
                    if (productData.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Description", fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily)
                        Text(
                            text = productData.description,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            fontFamily = MyCustomFontFamily,
                            lineHeight = 18.sp
                        )
                    }
                }

                FeedbackSection(
                    currentFeedback = feedback,
                    onFeedbackClick = { feedback = it }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun DetailRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text("$label : ", fontSize = 14.sp, fontFamily = MyCustomFontFamily)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily)
    }
}

@Composable
fun FeedbackSection(currentFeedback: Boolean?, onFeedbackClick: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.padding(20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Tried it? Did it Match?", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily)
            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { onFeedbackClick(true) }) {
                Icon(
                    imageVector = if (currentFeedback == true) Icons.Default.ThumbUp else Icons.Outlined.ThumbUpOffAlt,
                    contentDescription = null,
                    tint = if (currentFeedback == true) Color(0xFF4CAF50) else Color.Gray
                )
            }

            IconButton(onClick = { onFeedbackClick(false) }) {
                Icon(
                    imageVector = if (currentFeedback == false) Icons.Default.ThumbDown else Icons.Outlined.ThumbDownOffAlt,
                    contentDescription = null,
                    tint = if (currentFeedback == false) Color.Red else Color.Gray
                )
            }
        }
    }
}