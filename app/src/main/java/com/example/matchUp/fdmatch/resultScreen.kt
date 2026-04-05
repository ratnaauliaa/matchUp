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
    val lastUserMatch = viewModel.selectedMatches.lastOrNull()

    // 1. Logika untuk menghasilkan list rekomendasi yang akan ditampilkan & disimpan
    val recommendations = remember(lastUserMatch) {
        if (lastUserMatch != null) {
            viewModel.productsData.flatMap { brandDetail ->
                brandDetail.products.map { product ->
                    // Cari shade terbaik untuk setiap produk
                    val bestShade = product.shades.minByOrNull { targetShade ->
                        viewModel.calculateColorDistance(lastUserMatch.shade.hex, targetShade.hex)
                    }
                    MatchedProduct(
                        brand = brandDetail.brand,
                        productName = product.product_name,
                        shadeName = bestShade?.shade_name ?: "Match Not Found",
                        imageUrl = product.image
                    )
                }
            }.filter { matchedProd ->
                // Filter agar produk yang diinput user tidak muncul lagi di rekomendasi
                viewModel.selectedMatches.none { it.product.product_name == matchedProd.productName }
            }.take(10)
        } else {
            emptyList()
        }
    }

    // 2. Simpan ke History hanya SEKALI saat halaman terbuka dan data rekomendasi siap
    LaunchedEffect(Unit) {
        if (recommendations.isNotEmpty()) {
            viewModel.saveCurrentMatchToHistory(recommendations)
        }
    }

    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var specificProductSearch by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

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
                    text = "Result",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontFamily = MyCustomFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // --- SECTION 1: RECALL ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(20.dp), tint = Color(0xFFFFB800))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("You entered this matches:", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = MyCustomFontFamily)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

                viewModel.selectedMatches.forEach { matchData ->
                    MatchEnteredItem(
                        brandName = matchData.brandName,
                        name = matchData.product.product_name,
                        shade = matchData.shade.shade_name,
                        imageUrl = matchData.product.image
                    )
                }

                TextButton(onClick = { viewModel.clearCurrentSelection(); onAddMore() }) {
                    Text("Get better results by adding more matches.", color = Color(0xFF4285F4), fontSize = 12.sp, fontFamily = MyCustomFontFamily, textDecoration = TextDecoration.Underline)
                }

                Spacer(modifier = Modifier.height(15.dp))

                // --- SECTION 2: RECOMMENDATIONS ---
                if (specificProductSearch != null) {
                    ResultHeader(title = "Your best match:", icon = Icons.Default.FactCheck)
                    // ... (logika specific search tetap sama) ...
                } else {
                    ResultHeader(title = "Browse all recommendations:", icon = Icons.Default.FactCheck)

                    TextButton(onClick = { showSheet = true }) {
                        Text(text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Black)) { append("Look for a match in a ") }
                            withStyle(style = SpanStyle(color = Color(0xFF4285F4), textDecoration = TextDecoration.Underline)) { append("specific product.") }
                        }, fontSize = 12.sp, fontFamily = MyCustomFontFamily)
                    }

                    // TAMPILKAN REKOMENDASI DARI LIST YANG SUDAH DIBUAT DI ATAS
                    recommendations.forEach { item ->
                        RecommendationResultItem(
                            brand = item.brand,
                            product = item.productName,
                            shade = item.shadeName,
                            imageUrl = item.imageUrl,
                            onClick = { onNavigateToDetail(item.productName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // ModalBottomSheet tetap sama...
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                SearchProductSheet(onFindMatch = { _, product ->
                    specificProductSearch = product
                    showSheet = false
                })
            }
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun ResultHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFFFFB800))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp, fontFamily = MyCustomFontFamily, modifier = Modifier.weight(1f))
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
        Text("Find your match in a specific product:", fontWeight = FontWeight.Bold,  color = Color.Black, fontSize = 18.sp, fontFamily = MyCustomFontFamily)
        Text(
            "Select the brand/product that you want to find your best match in.",
            fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp), fontFamily = MyCustomFontFamily
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = brandInput,
            onValueChange = { brandInput = it },
            label = { Text("Brand") },
            placeholder = { Text("e.g. Wardah") },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(20.dp)
        )

        OutlinedTextField(
            value = productInput,
            onValueChange = { productInput = it },
            label = { Text("Product") },
            placeholder = { Text("Enter product name") },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(20.dp),
            trailingIcon = { Icon(Icons.Default.Search, null) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onFindMatch(brandInput, productInput) },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Find my matches", color = Color.Black, fontSize = 15.sp, fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MatchEnteredItem(brandName: String, name: String, shade: String, imageUrl: String) {
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
            Text(
                text = brandName.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                fontFamily = MyCustomFontFamily
            )
            Text(name, fontSize = 13.sp, color = Color.DarkGray, fontFamily = MyCustomFontFamily, maxLines = 1)
            Text("Your Shade: $shade", fontSize = 13.sp, color = Color.DarkGray, fontFamily = MyCustomFontFamily)
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
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF9F9F9))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text(product, fontSize = 13.sp, color = Color.DarkGray, maxLines = 1, fontFamily = MyCustomFontFamily)
            Text("Your Shade: $shade", fontSize = 13.sp, color = Color.DarkGray, fontFamily = MyCustomFontFamily)
        }
        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.LightGray)
    }
}