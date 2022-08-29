package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityProductDetailsBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.CartItem
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {

	private var binding : ActivityProductDetailsBinding? = null
	private lateinit var product : Product
	private lateinit var user : User

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityProductDetailsBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		FirestoreClass().getCurrentUserDetails(this)

		if (intent.hasExtra(Constants.INTENT_EXTRA_PRODUCT_KEY)) {
			product = intent.getParcelableExtra(Constants.INTENT_EXTRA_PRODUCT_KEY)!!
			setFields(product)

			binding?.btnAddToCart?.setOnClickListener {
				showProgressDialog()
				addToCart()
			}

			binding?.btnGoToCart?.setOnClickListener {
				startActivity(Intent(this, CartActivity::class.java))
			}
		} else {
			Toast.makeText(
				this,
				"An error occured during displaying product details.",
				Toast.LENGTH_SHORT
			).show()
		}
	}

	private fun addToCart() {
		val userHashMap = HashMap<String, Any>()
		val cart = user.cart
		if (cart.containsKey(product.id)) {
			val cartItem = cart[product.id]!!
			if (cartItem.quantity < product.stockQuantity.toInt()) {
				cart[product.id] = CartItem(cartItem.product, cartItem.quantity + 1)
			} else {
				hideProgressDialog()
				Toast.makeText(this, "You cannot exceed stock quantity of the product", Toast.LENGTH_SHORT).show()
				return
			}
		} else {
			cart[product.id] = CartItem(product, 1)
		}
		userHashMap[Constants.DATABASE_CART_KEY] = cart
		FirestoreClass().updateUser(this, userHashMap)
	}

	private fun setFields(product: Product) {

		if (product.image.isNotEmpty()) {
			binding?.ivNoPhoto?.visibility = View.GONE
			binding?.ivProductDetailImage?.visibility = View.VISIBLE
			GlideLoader(this).loadProductPicture(product.image, binding?.ivProductDetailImage!!, "fitCenter")
		} else {
			binding?.ivNoPhoto?.visibility = View.VISIBLE
			binding?.ivProductDetailImage?.visibility = View.GONE
		}

		val currentUserId = FirestoreClass().getCurrentUserId()

		if (product.stockQuantity == "0") {
			binding?.tvOutOfStock?.visibility = View.VISIBLE
			binding?.tvProductDetailsAvailableQuantity?.visibility = View.GONE

			if (product.userId == currentUserId) {
				binding?.btnAddToCart?.visibility = View.GONE
			} else {
				binding?.btnAddToCart?.setBackgroundResource(R.drawable.button_background_disabled)
				binding?.btnAddToCart?.isEnabled = false
			}
		} else {
			binding?.tvOutOfStock?.visibility = View.GONE
			binding?.tvProductDetailsAvailableQuantity?.visibility = View.VISIBLE
			binding?.tvProductDetailsAvailableQuantity?.text = product.stockQuantity

			if (product.userId == currentUserId) {
				binding?.btnAddToCart?.visibility = View.GONE
			} else {
				binding?.btnAddToCart?.isEnabled = true
			}
		}

		binding?.tvProductDetailsTitle?.text = product.name
		binding?.tvProductDetailsPrice?.text = "${product.price}€"
		binding?.tvProductDetailsDescription?.text = product.description
	}

	fun onUserDetailsLoadSuccessfuly(userDetails: User) {
		user = userDetails
	}

	fun onUserUpdateSuccess() {
		hideProgressDialog()
		Toast.makeText(this, "Product added to cart.", Toast.LENGTH_SHORT).show()
	}

	fun onUserUpdateFailure() {
		hideProgressDialog()
		Toast.makeText(this, "An error occurred during adding the product to the cart", Toast.LENGTH_SHORT).show()
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarProductDetailsActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarProductDetailsActivity?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}