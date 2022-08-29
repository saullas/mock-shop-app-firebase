package com.example.shopappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
	val userId : String = "",
	val userName : String = "",
	val name : String = "",
	val price : String = "",
	val stockQuantity : String = "",
	val description : String = "",
	var image : String = "",
	var id : String = ""
) : Parcelable
