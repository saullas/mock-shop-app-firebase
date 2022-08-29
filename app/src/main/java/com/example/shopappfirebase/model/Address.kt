package com.example.shopappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
	val userId : String = "",
	val name : String = "",
	val phoneNumber : String = "",
	val address : String = "",
	val zipCode : String = "",
	val additionalNote : String = "",
	val type : String = "",
	var id : String = ""
) : Parcelable
