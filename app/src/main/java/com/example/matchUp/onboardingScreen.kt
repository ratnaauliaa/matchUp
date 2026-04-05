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
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // --- Bagian Atas: Tombol Skip ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            // Tombol skip
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(onboardingPages.size - 1) }
                    }
                ) {
                    Text(
                        text = "Skip",
                        color = Color.Gray,
                        fontFamily = MyCustomFontFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp
                    )
                }
            }
        }

// --- Bagian Tengah: Pager ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = page.title,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    fontFamily = MyCustomFontFamily,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // --- Bagian Bawah: Indikator & Tombol ---
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
                        .width(if (isActive) 24.dp else 8.dp)
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
                .width(200.dp)
                .height(45.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(20.dp) // Rounded yang tidak terlalu tajam
        ) {
            Text(
                text = onboardingPages[pagerState.currentPage].buttonText,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold, // Tombol pakai Bold agar tegas
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(50.dp)) // Jarak aman ke bawah layar
    }
}

// --- PREVIEW SECTION ---

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingPreview() {
    OnboardingScreen(onFinished = {})
}