package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityCheckoutBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Address
import com.example.shopappfirebase.model.Order
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.ui.adapters.CartListAdapter

class CheckoutActivity : BaseActivity() {

	private var binding : ActivityCheckoutBinding? = null
	private var address : Address? = null
	private var userDetails: User? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityCheckoutBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		FirestoreClass().getCurrentUserDetails(this)

		if (intent.hasExtra("address")) {
			address = intent.getParcelableExtra("address")
		}

		if (address != null) {
			binding?.tvCheckoutAddressType?.text = address!!.type
			binding?.tvCheckoutFullName?.text = address!!.name
			binding?.tvCheckoutAddress?.text = "${address!!.address}, ${address!!.zipCode}"
			binding?.tvCheckoutAdditionalNote?.text = address!!.additionalNote
			binding?.tvMobileNumber?.text = address!!.phoneNumber

			binding?.btnPlaceOrder?.setOnClickListener {
				if (address != null && userDetails != null) {
					showProgressDialog()
					placeOrder()
				}
			}
		} else {
			Toast.makeText(this, "There was problem with your address", Toast.LENGTH_SHORT).show();
		}
	}

	override fun onResume() {
		super.onResume()
		if (userDetails != null) {
			showProgressDialog()
			showCartItems()
			hideProgressDialog()
		}
	}

	private fun placeOrder() {
		var totalPrice = 0.0

		for ((_, cartItem) in userDetails!!.cart.toList()) {
			totalPrice += (cartItem.product!!.price.toDouble() * cartItem.quantity)
		}

		val order = Order(
			FirestoreClass().getCurrentUserId(),
			address!!,
			ArrayList(userDetails!!.cart.values),
			"10",
			totalPrice.toString()
		)

		FirestoreClass().uploadOrder(this, order)
	}

	fun onOrderUploadSuccess() {
		FirestoreClass().updateProductQuantities(this, ArrayList(userDetails!!.cart.values))
	}

	fun onOrderUploadFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"An error occured during creation of your order.",
			Toast.LENGTH_SHORT
		).show()
	}

	private fun showCartItems() {
		if (userDetails!!.cart.isNotEmpty()) {

			val adapterCartProducts = CartListAdapter(this, ArrayList(userDetails!!.cart.values), this)
			binding?.rvCartListItems?.layoutManager = LinearLayoutManager(this)
			binding?.rvCartListItems?.setHasFixedSize(true)
			binding?.rvCartListItems?.adapter = adapterCartProducts

			var totalPrice = 0.0

			for ((_, cartItem) in userDetails!!.cart.toList()) {
				totalPrice += (cartItem.product!!.price.toDouble() * cartItem.quantity)
			}

			binding?.tvCheckoutSubTotal?.text = "${totalPrice}€"
			binding?.tvCheckoutShippingCharge?.text = "10.0€"
			binding?.tvCheckoutTotalAmount?.text = "${totalPrice + 10.0}€"
		}
	}

	fun onUserDetailsLoadSuccess(user: User) {
		userDetails = user
		onResume()
	}

	fun onUserDetailsLoadFailure() {
		Toast.makeText(
			this,
			"Error fetching cart data",
			Toast.LENGTH_SHORT
		).show()
		onBackPressed()
	}

	fun onProductQuantityUpdateSuccess() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"Your order was placed successfully.",
			Toast.LENGTH_SHORT
		).show()

		val intent = Intent(this, DashboardActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		startActivity(intent)
	}

	fun onProductQuantityUpdateFailure() {
		Toast.makeText(
			this,
			"An error occured during creation of your order.",
			Toast.LENGTH_SHORT
		).show()
		onBackPressed()
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