import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.onboardingPages
import kotlinx.coroutines.launch
import com.example.matchUp.ui.theme.MyCustomFontFamily



@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    // Tambahkan modifier di pagerState agar lebih stabil
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding() // Tambahkan ini agar tidak nabrak status bar (jam/baterai)
            .padding(horizontal = 20.dp)
    ) {
        // --- Bagian Atas: Tombol Skip ---
        Box(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(onboardingPages.size - 1) }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Skip",
                        color = Color.Gray,
                        fontFamily = MyCustomFontFamily,
                        fontWeight = FontWeight.Light, // Memakai light_font.ttf
                        fontSize = 14.sp
                    )
                }
            }
        }

        // --- Bagian Tengah: Pager ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically // Pastikan konten di tengah secara vertikal
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp), // Sedikit dikecilkan agar proporsional
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(32.dp)) // Jarak antar gambar ke judul diperbesar
                Text(
                    text = page.title,
                    fontSize = 26.sp, // Judul sedikit lebih besar
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp // Jarak antar baris biar enak dibaca
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal, // Pakai Normal untuk deskripsi biar mata gak capek
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // --- Bagian Bawah: Indikator & Tombol ---
        // Pindahkan Indikator ke bawah deskripsi dengan jarak yang pas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val isActive = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(if (isActive) 24.dp else 8.dp) // Indikator lonjong vs bulat lebih modern
                        .height(8.dp)
                        .background(
                            color = if (isActive) Color.Black else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth() // Buat tombol lebih lebar biar gampang diklik (UX)
                .padding(horizontal = 20.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(16.dp) // Rounded yang tidak terlalu tajam
        ) {
            Text(
                text = onboardingPages[pagerState.currentPage].buttonText,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold, // Tombol pakai Bold agar tegas
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(50.dp)) // Jarak aman ke bawah layar
    }
}

// --- PREVIEW SECTION ---

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingPreview() {
    // Berikan lambda kosong {} untuk onFinished agar preview bisa jalan
    OnboardingScreen(onFinished = {})
}