package com.example.matchUp.fdmatch

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.R
import com.example.matchUp.ui.theme.MyCustomFontFamily
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MatchViewModel,
    onBack: () -> Unit,
    onAddMore: () -> Unit
) {
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
                    IconButton(onClick = { /* Action Download */ }) {
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
            // --- SECTION 1: RECALL (Tampilkan apa yang diinput user) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome, // Ganti dari painterResource
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFFFB800)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("You entered this matches:", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            if (viewModel.selectedMatches.isEmpty()) {
                Text("No matches entered yet.", fontSize = 12.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)
            } else {
                viewModel.selectedMatches.forEach { matchData ->
                    MatchEnteredItem(
                        name = matchData.product.product_name,
                        shade = matchData.shade.shade_name
                    )
                }
            }

            TextButton(onClick = onAddMore) {
                Text("Get better results by adding more matches.", color = Color(0xFF4285F4), fontSize = 12.sp, fontFamily = MyCustomFontFamily)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION 2: RECOMMENDATIONS (Logika Hex Distance) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FactCheck, // Ikon tanda centang pada list
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFFFB800) // Warna emas/kuning
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Browse all recommendations:", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = MyCustomFontFamily)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            // Ambil input terakhir user sebagai patokan warna (Anchor)
            val lastUserMatch = viewModel.selectedMatches.lastOrNull()

            if (lastUserMatch != null) {
                // Filter produk yang BELUM dipilih user
                val recommendations = viewModel.productsData.flatMap { brandDetail ->
                    brandDetail.products.map { product -> brandDetail.brand to product }
                }.filter { pair ->
                    viewModel.selectedMatches.none { it.product.product_name == pair.second.product_name }
                }.take(10)

                recommendations.forEach { (brandName, product) ->
                    // LOGIKA: Cari shade di produk ini yang Hex-nya paling mirip dengan pilihan user
                    val bestShade = product.shades.minByOrNull { targetShade ->
                        calculateColorDistance(lastUserMatch.shade.hex, targetShade.hex)
                    }

                    RecommendationResultItem(
                        brand = brandName,
                        product = product.product_name,
                        shade = bestShade?.shade_name ?: "Match Not Found"
                    )
                }
            } else {
                Text("Please add a match first to see recommendations.", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Logika Matematika Euclidean Distance untuk membandingkan 2 Kode Hex
 */
fun calculateColorDistance(hex1: String, hex2: String): Double {
    return try {
        // Bersihkan string hex jika ada tanda #
        val h1 = hex1.replace("#", "")
        val h2 = hex2.replace("#", "")

        val r1 = h1.substring(0, 2).toInt(16)
        val g1 = h1.substring(2, 4).toInt(16)
        val b1 = h1.substring(4, 6).toInt(16)

        val r2 = h2.substring(0, 2).toInt(16)
        val g2 = h2.substring(2, 4).toInt(16)
        val b2 = h2.substring(4, 6).toInt(16)

        sqrt((r2 - r1).toDouble().pow(2) + (g2 - g1).toDouble().pow(2) + (b2 - b1).toDouble().pow(2))
    } catch (e: Exception) {
        Double.MAX_VALUE
    }
}

@Composable
fun MatchEnteredItem(name: String, shade: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = R.drawable.ic_fd), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.LightGray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = MyCustomFontFamily, maxLines = 1)
            Text("Your Shade: $shade", fontSize = 12.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)
        }
    }
}

@Composable
fun RecommendationResultItem(brand: String, product: String, shade: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { /* Detail Produk */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF9F9F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = R.drawable.ic_fd), contentDescription = null, modifier = Modifier.size(28.dp), tint = Color(0xFFFFD1E3))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = MyCustomFontFamily)
            Text(product, fontSize = 12.sp, color = Color.Gray, fontFamily = MyCustomFontFamily, maxLines = 1)
            Text("Your Shade: $shade", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, fontFamily = MyCustomFontFamily)
        }
        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp).rotate(180f), tint = Color.LightGray)
    }
}
