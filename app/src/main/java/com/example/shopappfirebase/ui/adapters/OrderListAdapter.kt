package com.example.shopappfirebase.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.databinding.OrderListItemLayoutBinding
import com.example.shopappfirebase.model.Order
import com.example.shopappfirebase.ui.activities.OrderDetailsActivity
import com.example.shopappfirebase.utils.Constants

class OrderListAdapter(
	private val context: Context,
	private val orderList: ArrayList<Order>) : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>(){

	inner class MyViewHolder(val binding: OrderListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val binding = OrderListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MyViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val order = orderList[position]

		holder.binding.tvTotalPrice.text = (order.subtotal_amount.toDouble() + order.shipping_charge.toDouble()).toString()

		holder.itemView.setOnClickListener {
			val intent = Intent(context, OrderDetailsActivity::class.java)
			intent.putExtra(Constants.INTENT_EXTRA_ORDER_KEY, order)
			context.startActivity(intent)
		}
	}

	override fun getItemCount(): Int {
		return orderList.size
	}
}