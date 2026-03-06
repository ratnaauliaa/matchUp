package com.example.matchUp.fdmatch

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.ui.theme.MyCustomFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    onAddMore: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    // --- TRIGGER SIMPAN KE HISTORY OTOMATIS ---
    LaunchedEffect(Unit) {
        viewModel.saveMatchToHistory()
    }

    val context = LocalContext.current

    // --- STATE MANAGEMENT ---
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var specificProductSearch by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Result", fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Simulasi Fitur Unduh
                        Toast.makeText(context, "Mendownload hasil match...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // --- SECTION 1: RECALL (History Input Sebelumnya) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFFFB800))
                Spacer(modifier = Modifier.width(8.dp))
                Text("You entered this matches:", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            viewModel.selectedMatches.forEach { matchData ->
                MatchEnteredItem(
                    name = matchData.product.product_name,
                    shade = matchData.shade.shade_name,
                    imageUrl = matchData.product.image
                )
            }

            // TOMBOL ADD MORE (Mereset inputan pencarian agar bersih)
            TextButton(
                onClick = {
                    viewModel.clearCurrentSelection()
                    onAddMore()
                }
            ) {
                Text("Get better results by adding more matches.", color = Color(0xFF4285F4), fontSize = 12.sp, fontFamily = MyCustomFontFamily)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION 2: RECOMMENDATIONS ---
            val lastUserMatch = viewModel.selectedMatches.lastOrNull()

            if (specificProductSearch != null) {
                // TAMPILAN JIKA USER FILTER PRODUK TERTENTU
                ResultHeader(title = "Your best match:", icon = Icons.Default.FactCheck)

                val foundProduct = viewModel.productsData.flatMap { it.products }
                    .find { it.product_name.equals(specificProductSearch, ignoreCase = true) }

                if (foundProduct != null && lastUserMatch != null) {
                    val bestShade = foundProduct.shades.minByOrNull {
                        // Menggunakan fungsi dari viewModel
                        viewModel.calculateColorDistance(lastUserMatch.shade.hex, it.hex)
                    }

                    RecommendationResultItem(
                        brand = "Recommended",
                        product = foundProduct.product_name,
                        shade = bestShade?.shade_name ?: "-",
                        imageUrl = foundProduct.image,
                        onClick = { onNavigateToDetail(foundProduct.product_name) }
                    )
                }

                TextButton(onClick = { specificProductSearch = null }) {
                    Text("Show all recommendations", color = Color(0xFF4285F4), fontSize = 12.sp, fontFamily = MyCustomFontFamily)
                }

            } else {
                // TAMPILAN STANDAR: SEMUA REKOMENDASI
                ResultHeader(title = "Browse all recommendations:", icon = Icons.Default.FactCheck)

                TextButton(onClick = { showSheet = true }) {
                    Text(
                        text = buildAnnotatedString {
                            append("Look for a match in a ")
                            withStyle(style = SpanStyle(color = Color(0xFF4285F4), textDecoration = TextDecoration.Underline)) {
                                append("specific product.")
                            }
                        },
                        fontSize = 12.sp,
                        fontFamily = MyCustomFontFamily
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                if (lastUserMatch != null) {
                    val recommendations = viewModel.productsData.flatMap { brandDetail ->
                        brandDetail.products.map { product -> brandDetail.brand to product }
                    }.filter { pair ->
                        viewModel.selectedMatches.none { it.product.product_name == pair.second.product_name }
                    }.take(10)

                    recommendations.forEach { (brandName, product) ->
                        val bestShade = product.shades.minByOrNull { targetShade ->
                            // Menggunakan fungsi dari viewModel
                            viewModel.calculateColorDistance(lastUserMatch.shade.hex, targetShade.hex)
                        }

                        RecommendationResultItem(
                            brand = brandName,
                            product = product.product_name,
                            shade = bestShade?.shade_name ?: "Match Not Found",
                            imageUrl = product.image,
                            onClick = { onNavigateToDetail(product.product_name) }
                        )
                    }
                }
            }
        }

        // --- POP UP SEARCH (MODAL BOTTOM SHEET) ---
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                SearchProductSheet(
                    onFindMatch = { _, product ->
                        specificProductSearch = product
                        showSheet = false
                    }
                )
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun ResultHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFFFFB800))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
    }
}

@Composable
fun SearchProductSheet(onFindMatch: (String, String) -> Unit) {
    var brandInput by remember { mutableStateOf("") }
    var productInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Find your match in a specific product:", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
        Text(
            "Select the brand/product that you want to find your best match in.",
            fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp), fontFamily = MyCustomFontFamily
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = brandInput,
            onValueChange = { brandInput = it },
            label = { Text("Brand") },
            placeholder = { Text("e.g. Wardah") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = productInput,
            onValueChange = { productInput = it },
            label = { Text("Product") },
            placeholder = { Text("Enter product name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { Icon(Icons.Default.Search, null) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onFindMatch(brandInput, productInput) },
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Find my matches", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MatchEnteredItem(name: String, shade: String, imageUrl: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = MyCustomFontFamily, maxLines = 1)
            Text("Your Shade: $shade", fontSize = 12.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)
        }
    }
}

@Composable
fun RecommendationResultItem(brand: String, product: String, shade: String, imageUrl: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF9F9F9))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(product, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
            Text("Your Shade: $shade", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        }
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.LightGray)
    }
}