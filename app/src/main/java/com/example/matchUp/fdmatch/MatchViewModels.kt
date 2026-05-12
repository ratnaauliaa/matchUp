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
// 1. MODEL DATA
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

// Model untuk menyimpan produk hasil match di dalam history
data class MatchedProduct(
    val brand: String,
    val productName: String,
    val shadeName: String,
    val imageUrl: String
)

data class HistoryItem(
    val date: String,
    val details: List<String>,
    val inputProducts: List<SelectedMatchData>, // Sudah disamakan
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
    var filteredRecommendations by mutableStateOf<List<Triple<String, Product, Shade>>>(emptyList())
    var personalizedRecommendations = mutableStateListOf<Pair<String, Product>>()

    // 1. Wishlist Logic
    // Di MatchViewModel.kt
    fun toggleSaveProduct(product: Product, currentBrand: String) {
        val index = savedProducts.indexOfFirst { it.product_name == product.product_name }

        if (index != -1) {
            savedProducts.removeAt(index)
        } else {
            // PAKSA brand-nya diisi dari parameter yang dikirim
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
            // Menggunakan .toList() untuk membuat salinan data saat ini (Immutable)
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

            // Save to state so it can be accessed by NavGraph or Screens
            undertoneResults = dataWrapper.undertone_results

            Log.d("MatchVM", "Successfully loaded undertone data from JSON")
        } catch (e: Exception) {
            Log.e("MatchVM", "Failed to load undertone JSON: ${e.message}")
        }
    }

// Di dalam MatchViewModel.kt

    // 1. Tambahkan variabel ini untuk menyimpan hasil yang sudah "dikunci"

    fun updateRecommendations(userUndertone: String) {
        // Jika data sudah ada, jangan acak ulang (ini kunci agar tidak ngerandom terus)
        if (filteredRecommendations.isNotEmpty() && userUndertone.isNotEmpty()) return

        if (userUndertone.isEmpty()) {
            filteredRecommendations = emptyList()
            return
        }

        val recommendedList = mutableListOf<Triple<String, Product, Shade>>()

        productsData.forEach { brandObj ->
            brandObj.products.forEach { product ->
                // Cari shade yang COCOK PERSIS dengan undertone
                val matchingShade = product.shades.find {
                    it.undertone.trim().equals(userUndertone.trim(), ignoreCase = true)
                }

                if (matchingShade != null) {
                    recommendedList.add(Triple(brandObj.brand, product, matchingShade))
                }
            }
        }

        // Shuffled HANYA dilakukan sekali di sini, lalu disimpan ke state
        filteredRecommendations = recommendedList.shuffled().take(5)
    }

    // Tambahkan fungsi ini untuk dipanggil saat user TEST ULANG
    fun resetRecommendations() {
        filteredRecommendations = emptyList()
        Log.d("MatchVM", "Recommendations has been reset for new test")
    }

    fun getProductsByBrand(brandName: String): List<Product> {
        val foundBrand = productsData.find { it.brand.trim().equals(brandName.trim(), ignoreCase = true) }
        return foundBrand?.products ?: emptyList()
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