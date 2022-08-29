package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityCartBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.CartItem
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.ui.adapters.CartListAdapter
import com.example.shopappfirebase.utils.Constants

class CartActivity : BaseActivity() {

	private var binding: ActivityCartBinding? = null
	private lateinit var user: User

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityCartBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		FirestoreClass().getCurrentUserDetails(this)

		binding?.btnCheckout?.setOnClickListener {
			val intent = Intent(this, AddressListActivity::class.java)
			intent.putExtra("previous_activity", "CartActivity")
			startActivity(intent)
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
		}
	}

	override fun onResume() {
		super.onResume()
		if (this::user.isInitialized) {
			showProgressDialog()
			showCartItems()
			hideProgressDialog()
		}
	}

	// TODO check if product still exists
	private fun showCartItems() {
		if (user.cart.isNotEmpty()) {
			binding?.tvNoCartItemFound?.visibility = View.GONE
			binding?.rvCartItemsList?.visibility = View.VISIBLE
			binding?.llCheckout?.visibility = View.VISIBLE

			val adapterCartProducts = CartListAdapter(this, ArrayList(user.cart.values), this)
			binding?.rvCartItemsList?.layoutManager = LinearLayoutManager(this)
			binding?.rvCartItemsList?.setHasFixedSize(true)
			binding?.rvCartItemsList?.adapter = adapterCartProducts

			var totalPrice = 0.0

			for ((_, cartItem) in user.cart.toList()) {
				totalPrice += (cartItem.product!!.price.toDouble() * cartItem.quantity)
			}

			binding?.tvSubTotal?.text = "${totalPrice}€"
			binding?.tvShippingCharge?.text = "10.0€"
			binding?.tvTotalAmount?.text = "${totalPrice + 10.0}€"
		} else {
			binding?.tvNoCartItemFound?.visibility = View.VISIBLE
			binding?.llCheckout?.visibility = View.GONE
			binding?.rvCartItemsList?.visibility = View.GONE
		}
	}

	fun deleteFromCart(productId: String) {
		val cart = user.cart
		if (cart.containsKey(productId)) {
			showProgressDialog()
			cart.remove(productId)
			val userHashMap = HashMap<String, Any>()
			userHashMap[Constants.DATABASE_CART_KEY] = cart
			FirestoreClass().updateUser(this, userHashMap)
		}
	}

	fun removeFromCart(product: Product) {
		val cart = user.cart
		val cartItem = cart[product.id]!!
		if (cartItem.quantity > 1) {
			showProgressDialog()
			cart[product.id] = CartItem(cartItem.product, cartItem.quantity - 1)
			val userHashMap = HashMap<String, Any>()
			userHashMap[Constants.DATABASE_CART_KEY] = cart
			FirestoreClass().updateUser(this, userHashMap)
		}
	}

	fun addToCart(product: Product) {
		val userHashMap = HashMap<String, Any>()
		val cart = user.cart
		val cartItem = cart[product.id]!!
		if (cartItem.quantity < product.stockQuantity.toInt()) {
			showProgressDialog()
			cart[product.id] = CartItem(cartItem.product, cartItem.quantity + 1)
			userHashMap[Constants.DATABASE_CART_KEY] = cart
			FirestoreClass().updateUser(this, userHashMap)
		} else {
			Toast.makeText(
				this,
				"You cannot exceed stock quantity of the product",
				Toast.LENGTH_SHORT
			).show()
		}
	}

	fun onUserDetailsLoadSuccessfuly(userData: User) {
		user = userData
		onResume()
	}

	fun onUserDetailsLoadFailure() {
		Toast.makeText(this, "Error fetching cart data", Toast.LENGTH_SHORT).show()
		onBackPressed()
	}

	fun onUserUpdateSuccess() {
		hideProgressDialog()
		Toast.makeText(this, "Cart updated successfully", Toast.LENGTH_SHORT).show()
		showCartItems()
	}

	fun onUserUpdateFailure() {
		hideProgressDialog()
		Toast.makeText(this, "An error occurred during updating your cart", Toast.LENGTH_SHORT).show()
		showCartItems()
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarCartListActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarCartListActivity?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}