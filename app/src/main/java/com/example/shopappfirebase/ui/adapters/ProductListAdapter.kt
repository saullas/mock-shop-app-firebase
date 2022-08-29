package com.example.shopappfirebase.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ProductListItemLayoutBinding
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.ui.activities.ProductDetailsActivity
import com.example.shopappfirebase.ui.fragments.ProductFragment
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader

class ProductListAdapter(
	private val context: Context,
	private val productList: ArrayList<Product>,
	private val productFragment: ProductFragment) : RecyclerView.Adapter<ProductListAdapter.MyViewHolder>(){

	inner class MyViewHolder(val binding: ProductListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val binding = ProductListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MyViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val product = productList[position]

		GlideLoader(context).loadProductPicture(product.image, holder.binding.ivItemImage, "centerCrop")
		holder.binding.tvItemName.text = product.name
		holder.binding.tvItemPrice.text = "${product.price}€"

		holder.binding.ivDeleteProduct.setOnClickListener {
			productFragment.showDeleteProductAlertDialog(product.id)
		}

		holder.binding.ivEditProduct.setOnClickListener {
			productFragment.editProduct(product)
		}

		holder.binding.clParent.setOnClickListener {
			val intent = Intent(context, ProductDetailsActivity::class.java)
			intent.putExtra(Constants.INTENT_EXTRA_PRODUCT_KEY, product)
			context.startActivity(intent)
		}
	}

	override fun getItemCount(): Int {
		return productList.size
	}
}