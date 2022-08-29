package com.example.shopappfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
	val userId: String = "",
	val shippingAddress: Address = Address(),
	val items: ArrayList<CartItem> = ArrayList(),
	val shipping_charge: String = "",
	val subtotal_amount: String = "",
	var id: String = "",
) : Parcelable
