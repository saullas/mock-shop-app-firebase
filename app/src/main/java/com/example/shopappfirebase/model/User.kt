package com.example.shopappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profileCompleted: Boolean = false,
    var cart: HashMap<String, CartItem> = HashMap()
): Parcelable
