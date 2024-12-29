package com.example.pharmacy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val imageRes: Int,
    val title: String,
    val price: Int,
    val category: String,
    var quantity: Int = 0
) : Parcelable
