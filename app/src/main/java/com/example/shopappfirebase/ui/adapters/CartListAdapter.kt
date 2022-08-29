package com.example.shopappfirebase.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.CartItemLayoutBinding
import com.example.shopappfirebase.model.CartItem
import com.example.shopappfirebase.ui.activities.CartActivity
import com.example.shopappfirebase.ui.activities.CheckoutActivity
import com.example.shopappfirebase.ui.activities.OrderDetailsActivity
import com.example.shopappfirebase.ui.activities.ProductDetailsActivity
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader


class CartListAdapter(
	private val context: Context,
	private val cartItemList: ArrayList<CartItem>,
	private val activity: Activity
) : RecyclerView.Adapter<CartListAdapter.MyViewHolder>(){

	inner class MyViewHolder(val binding: CartItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val binding = CartItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MyViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val cartItem = cartItemList[position]
		val product = cartItem.product!!

		GlideLoader(context).loadProductPicture(product.image, holder.binding.ivCartItemImage, "fitCenter")
		holder.binding.tvCartItemTitle.text = product.name
		holder.binding.tvCartItemPrice.text = "${product.price}€"
		holder.binding.tvCartQuantity.text = cartItem.quantity.toString()

		when (activity) {
			is CartActivity -> {
				holder.binding.ibDeleteCartItem.setOnClickListener {
					activity.deleteFromCart(product.id)
				}

				holder.binding.ibAddCartItem.setOnClickListener {
					activity.addToCart(product)
				}

				holder.binding.ibRemoveCartItem.setOnClickListener {
					activity.removeFromCart(product)
				}
				holder.binding.layoutParent.setOnClickListener {
					val intent = Intent(context, ProductDetailsActivity::class.java)
					intent.putExtra(Constants.INTENT_EXTRA_PRODUCT_KEY, product)
					activity.startActivity(intent)
					activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
				}
			}
			is CheckoutActivity -> {
				holder.binding.ibAddCartItem.visibility = View.INVISIBLE
				holder.binding.ibDeleteCartItem.visibility = View.INVISIBLE
				holder.binding.ibRemoveCartItem.visibility = View.INVISIBLE

				holder.binding.layoutParent.setOnClickListener {
					val intent = Intent(context, ProductDetailsActivity::class.java)
					intent.putExtra(Constants.INTENT_EXTRA_PRODUCT_KEY, product)
					activity.startActivity(intent)
					activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
				}
			}
			is OrderDetailsActivity -> {
				holder.binding.ibAddCartItem.visibility = View.INVISIBLE
				holder.binding.ibRemoveCartItem.visibility = View.INVISIBLE
				holder.binding.ibDeleteCartItem.visibility = View.INVISIBLE
			}
		}

	}

	override fun getItemCount(): Int {
		return cartItemList.size
	}
}