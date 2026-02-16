package com.example.matchUp.fdmatch

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 1. Cetakan untuk Brand (Isinya: Nama Brand dan Daftar Produk)
data class Brand(
    val brand: String,
    val products: List<Product>
)

// 2. Cetakan untuk Produk (Isinya: Nama, Gambar, Deskripsi, dan Daftar Warna)
@Parcelize
data class Product(
    val product_name: String,
    val image: String,
    val description: String,
    val shades: List<Shade>
) : Parcelable

// 3. Cetakan untuk Warna/Shade (Isinya: Nama Shade, Hex warna, dll)
@Parcelize
data class Shade(
    val shade_name: String,
    val hex: String,
    val undertone: String,
    val skintone: String
) : Parcelable