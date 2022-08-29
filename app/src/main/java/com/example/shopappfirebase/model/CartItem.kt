package com.example.shopappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
	val product : Product? = null,
	val quantity : Int = 0
) : Parcelable
