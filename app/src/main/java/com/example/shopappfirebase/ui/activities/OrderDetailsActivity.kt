package com.example.shopappfirebase.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityOrderDetailsBinding
import com.example.shopappfirebase.model.Order
import com.example.shopappfirebase.ui.adapters.CartListAdapter
import com.example.shopappfirebase.utils.Constants

class OrderDetailsActivity : AppCompatActivity() {

	private var binding : ActivityOrderDetailsBinding? = null
	private var order : Order? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		if (intent.hasExtra(Constants.INTENT_EXTRA_ORDER_KEY)) {
			order = intent.getParcelableExtra(Constants.INTENT_EXTRA_ORDER_KEY)
			displayDetails(order!!)
		} else {
			Toast.makeText(this, "Error rendering order details", Toast.LENGTH_SHORT).show();
		}
	}

	private fun displayDetails(order: Order) {
		val address = order.shippingAddress
		binding?.tvCheckoutAddressType?.text = address.type
		binding?.tvCheckoutFullName?.text = address.name
		binding?.tvCheckoutAddress?.text = "${address.address}, ${address.zipCode}"
		binding?.tvCheckoutAdditionalNote?.text = address.additionalNote
		binding?.tvMobileNumber?.text = address.phoneNumber

		val adapterCartProducts = CartListAdapter(this, order.items, this)
		binding?.rvCartListItems?.layoutManager = LinearLayoutManager(this)
		binding?.rvCartListItems?.setHasFixedSize(true)
		binding?.rvCartListItems?.adapter = adapterCartProducts

		var totalPrice = 0.0

		for (cartItem in order.items) {
			totalPrice += (cartItem.product!!.price.toDouble() * cartItem.quantity)
		}

		binding?.tvCheckoutSubTotal?.text = "${totalPrice}€"
		binding?.tvCheckoutShippingCharge?.text = "10.0€"
		binding?.tvCheckoutTotalAmount?.text = "${totalPrice + 10.0}€"
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarCheckoutActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarCheckoutActivity?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}