package com.example.matchUp.fdmatch

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import com.example.matchUp.UndertoneResult

// =====================================================================
// 1. MODEL DATA (TETAP SAMA PERSIS SESUAI KODE ASLIMU)
// =====================================================================

data class BrandInfo(
    val name: String,
    val status: String
)

data class SelectedMatchData(
    val brandName: String,
    val product: Product,
    val shade: Shade
)

data class HistoryData(
    val date: String,
    val details: List<String>
)

data class Article(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val content: String,
    val sourceUrl: String
)

data class UndertoneDataWrapper(
    val undertone_results: List<UndertoneResult>
)

data class MatchedProduct(
    val brand: String,
    val productName: String,
    val shadeName: String,
    val imageUrl: String
)

data class HistoryItem(
    val date: String,
    val details: List<String>,
    val inputProducts: List<SelectedMatchData>,
    val matchedProducts: List<MatchedProduct>
)

data class Rgb(val r: Int, val g: Int, val b: Int)

// =====================================================================
// 2. VIEWMODEL
// =====================================================================

class MatchViewModel : ViewModel() {

    // --- USER PROFILE STATE ---
    var isLoggedIn by mutableStateOf(false)
    var userName by mutableStateOf("Guest")
    var userEmail by mutableStateOf("")

    // --- DATA STATE ---
    var allBrandList by mutableStateOf<List<BrandInfo>>(listOf())
    var productsData by mutableStateOf<List<BrandDetail>>(listOf())

    // --- SELECTION STATE ---
    var selectedBrandName by mutableStateOf("")
    var selectedProduct by mutableStateOf<Product?>(null)
    var selectedShade by mutableStateOf<Shade?>(null)

    var selectedMatches = mutableStateListOf<SelectedMatchData>()
    var historyList = mutableStateListOf<HistoryItem>()
    private var lastSavedMatches: List<SelectedMatchData> = listOf()

    // --- WISHLIST & INSIGHT STATE ---
    var searchQuery by mutableStateOf("")
    var selectedInsightCategory by mutableStateOf("All")
    var allArticles by mutableStateOf<List<Article>>(listOf())
    var savedProducts = mutableStateListOf<Product>()

    // --- TEST UNDERTONE STATE ---
    var warmScore by mutableStateOf(0)
    var coolScore by mutableStateOf(0)
    var neutralScore by mutableStateOf(0)
    var finalUndertone by mutableStateOf("")
    var undertoneResults by mutableStateOf<List<UndertoneResult>>(listOf())

    // Simpan data rekomendasi umum
    var filteredRecommendations by mutableStateOf<List<Triple<String, Product, Shade>>>(kotlin.collections.emptyList())
    var personalizedRecommendations = mutableStateListOf<Pair<String, Product>>()

    // 1. Wishlist Logic
    fun toggleSaveProduct(product: Product, currentBrand: String) {
        val index = savedProducts.indexOfFirst { it.product_name == product.product_name }

        if (index != -1) {
            savedProducts.removeAt(index)
        } else {
            val fixedProduct = product.copy(brand = currentBrand)
            savedProducts.add(0, fixedProduct)
        }
    }

    fun isProductSaved(productName: String): Boolean {
        return savedProducts.any { it.product_name == productName }
    }

    // 2. Article Logic
    fun getFilteredArticles(): List<Article> {
        val filteredByCategory = if (selectedInsightCategory == "All") {
            allArticles
        } else {
            allArticles.filter { it.category.equals(selectedInsightCategory, ignoreCase = true) }
        }

        return if (searchQuery.isEmpty()) {
            filteredByCategory
        } else {
            filteredByCategory.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // 3. Undertone Logic
    fun resetScores() {
        warmScore = 0
        coolScore = 0
        neutralScore = 0
    }

    fun getResult(): String {
        return when {
            warmScore > coolScore && warmScore > neutralScore -> "Warm"
            coolScore > warmScore && coolScore > neutralScore -> "Cool"
            else -> "Neutral"
        }
    }

    fun setUserProfile(name: String, email: String) {
        userName = name
        userEmail = email
    }

    fun updateUserName(newName: String) {
        if (newName.isNotEmpty()) userName = newName
    }

    // 4. Match & History Logic
    fun addMatch(brandName: String, product: Product, shade: Shade) {
        val isAlreadyAdded = selectedMatches.any {
            it.shade.shade_name == shade.shade_name && it.product.product_name == product.product_name
        }
        if (!isAlreadyAdded) {
            selectedMatches.add(SelectedMatchData(brandName, product, shade))
        }
    }

    fun removeMatch(index: Int) {
        if (index in selectedMatches.indices) selectedMatches.removeAt(index)
    }

    fun clearCurrentSelection() {
        selectedBrandName = ""
        selectedProduct = null
        selectedShade = null
    }

    fun resetSelection() {
        selectedProduct = null
        selectedShade = null
    }

    fun clearMatchSelection() {
        selectedMatches.clear()
        selectedProduct = null
        selectedShade = null
        selectedBrandName = ""
    }

    fun saveCurrentMatchToHistory(results: List<MatchedProduct>) {
        val currentDetails = selectedMatches.map {
            "${it.brandName} - ${it.product.product_name} (${it.shade.shade_name})"
        }

        val lastHistory = historyList.firstOrNull()
        if (lastHistory != null && lastHistory.details == currentDetails) {
            return
        }

        val newHistory = HistoryItem(
            date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            details = currentDetails,
            inputProducts = selectedMatches.toList(),
            matchedProducts = results
        )
        historyList.add(0, newHistory)
    }

    fun clearAllHistory() {
        historyList.clear()
    }

    // 5. Data Loading
    fun loadData(context: Context) {
        val gson = Gson()
        try {
            val brandsJson = context.assets.open("brands_list.json").bufferedReader().use { it.readText() }
            allBrandList = gson.fromJson(brandsJson, object : TypeToken<List<BrandInfo>>() {}.type)

            val productsJson = context.assets.open("products_data.json").bufferedReader().use { it.readText() }
            productsData = gson.fromJson(productsJson, object : TypeToken<List<BrandDetail>>() {}.type)

            val articlesJson = context.assets.open("articles_data.json").bufferedReader().use { it.readText() }
            allArticles = gson.fromJson(articlesJson, object : TypeToken<List<Article>>() {}.type)
        } catch (e: Exception) {
            Log.e("MatchVM", "Error loading data: ${e.message}")
        }
    }

    fun loadUndertoneResultsFromJson(context: Context) {
        try {
            val jsonString = context.assets.open("undertone_data.json").bufferedReader().use { it.readText() }
            val dataWrapper: UndertoneDataWrapper = Gson().fromJson(jsonString, object : TypeToken<UndertoneDataWrapper>() {}.type)

            undertoneResults = dataWrapper.undertone_results
            Log.d("MatchVM", "Successfully loaded undertone data from JSON")
        } catch (e: Exception) {
            Log.e("MatchVM", "Failed to load undertone JSON: ${e.message}")
        }
    }

    // DIBAIKI: Tambahkan filter kategori dasar "foundation" agar hasil rekomendasi tes murni kosmetik wajah
    fun updateRecommendations(userUndertone: String) {
        if (filteredRecommendations.isNotEmpty() && userUndertone.isNotEmpty()) return

        if (userUndertone.isEmpty()) {
            filteredRecommendations = kotlin.collections.emptyList()
            return
        }

        // Buat dua keranjang terpisah agar adil
        val foundationList = mutableListOf<Triple<String, Product, Shade>>()
        val lipsList = mutableListOf<Triple<String, Product, Shade>>()

        productsData.forEach { brandObj ->
            brandObj.products.forEach { product ->
                val matchingShade = product.shades.find {
                    it.undertone.trim().equals(userUndertone.trim(), ignoreCase = true)
                }

                if (matchingShade != null) {
                    if (product.category.equals("foundation", ignoreCase = true)) {
                        foundationList.add(Triple(brandObj.brand, product, matchingShade))
                    } else if (product.category.equals("lips", ignoreCase = true)) {
                        lipsList.add(Triple(brandObj.brand, product, matchingShade))
                    }
                }
            }
        }

        // Ambil maksimal 3 foundation acak dan 2 lips acak (Total tetap 5 rekomendasi)
        val randomFoundations = foundationList.shuffled().take(3)
        val randomLips = lipsList.shuffled().take(2)

        // Gabungkan keduanya lalu acak posisinya di Home biar estetik
        val finalCombinedList = (randomFoundations + randomLips).shuffled()

        filteredRecommendations = finalCombinedList
    }


    fun resetRecommendations() {
        filteredRecommendations = kotlin.collections.emptyList()
        Log.d("MatchVM", "Recommendations has been reset for new test")
    }

    // DIBAIKI: Fungsi ini awalnya dipanggil bebas, pastikan kamu selalu memakai getProductsByBrandAndCategory di screen list utama
    fun getProductsByBrand(brandName: String): List<Product> {
        val foundBrand = productsData.find { it.brand.trim().equals(brandName.trim(), ignoreCase = true) }
        return foundBrand?.products ?: kotlin.collections.emptyList()
    }

    fun getProductsByBrandAndCategory(brandName: String, category: String): List<Product> {
        val foundBrand = productsData.find { it.brand.trim().equals(brandName.trim(), ignoreCase = true) }
        return foundBrand?.products?.filter { it.category.equals(category, ignoreCase = true) } ?: kotlin.collections.emptyList()
    }

    fun calculateColorDistance(hex1: String, hex2: String): Double {
        return try {
            val h1 = hexToRgb(hex1)
            val h2 = hexToRgb(hex2)
            sqrt((h2.r - h1.r).toDouble().pow(2) +
                    (h2.g - h1.g).toDouble().pow(2) +
                    (h2.b - h1.b).toDouble().pow(2))
        } catch (e: Exception) {
            Double.MAX_VALUE
        }
    }

    private fun hexToRgb(hex: String): Rgb {
        val h = hex.replace("#", "").let { if (it.length == 3) it.map { c -> "$c$c" }.joinToString("") else it }
        return Rgb(h.substring(0, 2).toInt(16), h.substring(2, 4).toInt(16), h.substring(4, 6).toInt(16))
    }
}