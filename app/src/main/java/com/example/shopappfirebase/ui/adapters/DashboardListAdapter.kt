package com.example.shopappfirebase.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.databinding.DashboardItemLayoutBinding
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.ui.activities.ProductDetailsActivity
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader

class DashboardListAdapter(
	private val context: Context,
	private val productList: ArrayList<Product>) : RecyclerView.Adapter<DashboardListAdapter.MyViewHolder>(){

	inner class MyViewHolder(val binding: DashboardItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val binding = DashboardItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MyViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val product = productList[position]

		GlideLoader(context).loadProductPicture(product.image, holder.binding.ivItemImage, "fitCenter")
		holder.binding.tvItemName.text = product.name
		holder.binding.tvItemPrice.text = "${product.price}€"

		holder.binding.rlParent.setOnClickListener {
			val intent = Intent(context, ProductDetailsActivity::class.java)
			intent.putExtra(Constants.INTENT_EXTRA_PRODUCT_KEY, product)
			context.startActivity(intent)
		}
	}

	override fun getItemCount(): Int {
		return productList.size
	}
}