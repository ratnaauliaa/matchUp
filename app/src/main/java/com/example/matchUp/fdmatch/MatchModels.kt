package com.example.matchUp.fdmatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 1. Cetakan untuk Brand
data class BrandDetail(
    val brand: String,
    val products: List<Product> = emptyList()
)

// 2. Cetakan untuk Produk
@Parcelize
data class Product(
    val id: Int = 0,
    val brand: String = "",
    val product_name: String,
    val category: String = "",
    val image: String,
    val description: String = "",
    val shades: List<Shade> = emptyList()
) : Parcelable

// 3. Cetakan untuk Warna/Shade
@Parcelize
data class Shade(
    val shade_name: String,
    val hex: String,
    val undertone: String = "",
    val skintone: String = ""
) : Parcelable