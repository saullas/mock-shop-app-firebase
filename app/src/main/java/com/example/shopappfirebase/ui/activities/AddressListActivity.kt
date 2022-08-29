package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityAddressListBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Address
import com.example.shopappfirebase.ui.adapters.AddressListAdapter
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import java.util.ArrayList

class AddressListActivity : BaseActivity() {

	private var binding : ActivityAddressListBinding? = null
	private var selectAddress = false // from cart activity, you need to select address

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityAddressListBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		binding?.tvAddAddress?.setOnClickListener {
			startActivity(Intent(this, AddEditAddressActivity::class.java))
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
		}

		if (intent.hasExtra("previous_activity") &&
			intent.getStringExtra("previous_activity") == "CartActivity") {
			binding?.tvTitle?.text = "SELECT ADDRESS"
			selectAddress = true
		}
	}

	override fun onResume() {
		super.onResume()
		showProgressDialog()
		getAddressList()
	}

	private fun getAddressList() {
		FirestoreClass().getUserAddresses(this)
	}

	fun onAddressListFetchSuccess(addressList: ArrayList<Address>) {
		hideProgressDialog()

		if (addressList.isNotEmpty()) {
			binding?.tvNoAddressFound!!.visibility = View.GONE
			binding?.rvAddressList!!.visibility = View.VISIBLE

			val adapterUserAddresses = AddressListAdapter(this, addressList, selectAddress)
			binding?.rvAddressList!!.layoutManager = LinearLayoutManager(this)
			binding?.rvAddressList!!.setHasFixedSize(true)
			binding?.rvAddressList!!.adapter = adapterUserAddresses

			if (!selectAddress) {
				val editSwipeHandler = object:SwipeToEditCallback(this) {
					override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
						adapterUserAddresses.notifyEditItem(
							this@AddressListActivity,
							viewHolder.adapterPosition
						)
					}
				}

				val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
				editItemTouchHelper.attachToRecyclerView(binding?.rvAddressList)

				val deleteSwipeHandler = object: SwipeToDeleteCallback(this) {
					override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
						showProgressDialog()
						FirestoreClass().deleteAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
					}
				}

				val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
				deleteItemTouchHelper.attachToRecyclerView(binding?.rvAddressList)
			}
		} else {
			binding?.tvNoAddressFound!!.visibility = View.VISIBLE
			binding?.rvAddressList!!.visibility = View.GONE
		}
	}

	fun onAddressListFetchFailure() {
		hideProgressDialog()
	}

	fun onAddressDeleteSuccess() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"Address deleted successfully",
			Toast.LENGTH_SHORT
		).show()
		onResume()
	}

	fun onAddressDeleteFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"An error occurred during deleting the address",
			Toast.LENGTH_SHORT
		).show()
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarAddressListActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarAddressListActivity?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	override fun onBackPressed() {
		super.onBackPressed()
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}