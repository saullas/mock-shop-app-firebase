package com.example.shopappfirebase.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.AddressItemLayoutBinding
import com.example.shopappfirebase.model.Address
import com.example.shopappfirebase.ui.activities.AddEditAddressActivity
import com.example.shopappfirebase.ui.activities.AddressListActivity
import com.example.shopappfirebase.ui.activities.CheckoutActivity
import com.example.shopappfirebase.utils.Constants

class AddressListAdapter(
	private val context: Context,
	private val addressList: ArrayList<Address>,
	private val selectAddress: Boolean) : RecyclerView.Adapter<AddressListAdapter.MyViewHolder>(){

	inner class MyViewHolder(val binding: AddressItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val binding = AddressItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MyViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val address = addressList[position]

		holder.binding.tvAddressFullName.text = address.name
		holder.binding.tvAddressType.text = address.type
		holder.binding.tvAddressMobileNumber.text = address.phoneNumber
		holder.binding.tvAddressDetails.text = "${address.address}, ${address.zipCode}"

		if (selectAddress) {
			holder.itemView.foreground = with(TypedValue()) {
				context.theme.resolveAttribute(
					android.R.attr.selectableItemBackground, this, true)
				ContextCompat.getDrawable(context, resourceId)
			}
			holder.itemView.setOnClickListener {
				val intent = Intent(context, CheckoutActivity::class.java)
				intent.putExtra("address", address)
				context.startActivity(intent)
			}
		}
	}

	fun notifyEditItem(activity: Activity, position: Int) {
		val intent = Intent(context, AddEditAddressActivity::class.java)
		intent.putExtra(Constants.INTENT_EXTRA_ADDRESS_KEY, addressList[position])
		activity.startActivity(intent)
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
		notifyItemChanged(position) // this should be called everytime you change an item
	}

	override fun getItemCount(): Int {
		return addressList.size
	}
}