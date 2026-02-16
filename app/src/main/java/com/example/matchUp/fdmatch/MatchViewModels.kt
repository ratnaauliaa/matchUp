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
import kotlin.math.pow
import kotlin.math.sqrt

// 1. Model untuk JSON list brand (brands_list.json)
data class BrandInfo(
    val name: String,
    val status: String
)

// 2. Model untuk JSON data produk lengkap (products_data.json)
data class BrandDetail(
    val brand: String,
    val products: List<Product> = emptyList()
)

// 3. Model untuk menampung rincian yang dipilih user
data class SelectedMatchData(
    val brandName: String,
    val product: Product,
    val shade: Shade
)

class MatchViewModel : ViewModel() {

    // State untuk Nama User
    var userName by mutableStateOf("User")

    // State untuk Data Brand dan Produk
    var allBrandList by mutableStateOf<List<BrandInfo>>(listOf())
    var productsData by mutableStateOf<List<BrandDetail>>(listOf())

    // State untuk Seleksi saat ini
    var selectedBrandName by mutableStateOf("")
    var selectedProduct by mutableStateOf<Product?>(null)
    var selectedShade by mutableStateOf<Shade?>(null)

    // List ringkasan untuk menampung beberapa pilihan (yang muncul di pop-up)
    var selectedMatches = mutableStateListOf<SelectedMatchData>()

    // Fungsi untuk update nama user
    fun updateUserName(newName: String) {
        if (newName.isNotEmpty()) {
            userName = newName
        }
    }

    // PERBAIKAN: Fungsi untuk menambah kecocokan (Match)
    fun addMatch(brandName: String, product: Product, shade: Shade) {
        // Cek agar tidak ada duplikat shade yang sama dari produk yang sama
        val isAlreadyAdded = selectedMatches.any {
            it.shade.shade_name == shade.shade_name && it.product.product_name == product.product_name
        }

        if (!isAlreadyAdded) {
            // Gunakan SelectedMatchData (sesuai nama data class di atas)
            selectedMatches.add(SelectedMatchData(brandName, product, shade))
        }
    }

    // Fungsi untuk menghapus kecocokan dari list
    fun removeMatch(index: Int) {
        if (index in selectedMatches.indices) {
            selectedMatches.removeAt(index)
        }
    }

    // Fungsi memuat file JSON dari Assets
    fun loadData(context: Context) {
        val gson = Gson()

        // Muat list brand aktif/tidak aktif
        try {
            val brandsJson = context.assets.open("brands_list.json").bufferedReader().use { it.readText() }
            val brandListType = object : TypeToken<List<BrandInfo>>() {}.type
            allBrandList = gson.fromJson(brandsJson, brandListType)
            Log.d("MatchVM", "✅ Brand List Loaded: ${allBrandList.size} items")
        } catch (e: Exception) {
            Log.e("MatchVM", "❌ Error loading brands_list: ${e.message}")
        }

        // Muat detail produk, brand, dan shade
        try {
            val productsJson = context.assets.open("products_data.json").bufferedReader().use { it.readText() }
            val productsType = object : TypeToken<List<BrandDetail>>() {}.type
            productsData = gson.fromJson(productsJson, productsType)
            Log.d("MatchVM", "✅ Product Data Loaded: ${productsData.size} brands")
        } catch (e: Exception) {
            Log.e("MatchVM", "❌ Error loading products_data: ${e.message}")
        }
    }

    // Fungsi pencarian produk berdasarkan nama brand
    fun getProductsByBrand(brandName: String): List<Product> {
        if (productsData.isEmpty()) return emptyList()

        val foundBrand = productsData.find {
            it.brand.trim().equals(brandName.trim(), ignoreCase = true)
        }
        return foundBrand?.products ?: emptyList()
    }

    // Di dalam MatchViewModel.kt
    fun resetSelection() {
        selectedProduct = null
        selectedShade = null
        // selectedBrandName = "" // Jika perlu
    }

    /**
     * Algoritma Pencocokan Warna (Euclidean Distance)
     * Digunakan nanti di Result Screen untuk mencari shade paling mirip
     */
    fun calculateColorDistance(hex1: String, hex2: String): Double {
        return try {
            val h1 = hex1.replace("#", "").let {
                if (it.length == 3) it.map { c -> "$c$c" }.joinToString("") else it
            }
            val h2 = hex2.replace("#", "").let {
                if (it.length == 3) it.map { c -> "$c$c" }.joinToString("") else it
            }

            val r1 = h1.substring(0, 2).toInt(16)
            val g1 = h1.substring(2, 4).toInt(16)
            val b1 = h1.substring(4, 6).toInt(16)

            val r2 = h2.substring(0, 2).toInt(16)
            val g2 = h2.substring(2, 4).toInt(16)
            val b2 = h2.substring(4, 6).toInt(16)

            sqrt(
                (r2 - r1).toDouble().pow(2) +
                        (g2 - g1).toDouble().pow(2) +
                        (b2 - b1).toDouble().pow(2)
            )
        } catch (e: Exception) {
            Double.MAX_VALUE
        }
    }
}