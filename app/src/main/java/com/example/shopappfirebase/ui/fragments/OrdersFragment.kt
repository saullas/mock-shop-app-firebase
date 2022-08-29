package com.example.shopappfirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.FragmentOrdersBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Order
import com.example.shopappfirebase.ui.activities.CartActivity
import com.example.shopappfirebase.ui.activities.SettingsActivity
import com.example.shopappfirebase.ui.adapters.OrderListAdapter
import com.example.shopappfirebase.ui.adapters.ProductListAdapter

class OrdersFragment : BaseFragment() {

	private var _binding: FragmentOrdersBinding? = null

	// This property is only valid between onCreateView and
	// onDestroyView.
	private val binding get() = _binding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// If we want to use the option menu in fragment we need to add it
		setHasOptionsMenu(true)
	}

	override fun onResume() {
		super.onResume()
		FirestoreClass().getUserOrders(this)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentOrdersBinding.inflate(inflater, container, false)
		return binding.root
	}

	fun onUserOrdersLoadSuccess(orderList: ArrayList<Order>) {
		if (orderList.size > 0) {
			binding.rvOrders.visibility = View.VISIBLE
			binding.tvNoOrders.visibility = View.GONE

			val orderListAdapter = OrderListAdapter(requireActivity(), orderList)
			binding.rvOrders.layoutManager = LinearLayoutManager(activity)
			binding.rvOrders.setHasFixedSize(true)
			binding.rvOrders.adapter = orderListAdapter
		} else {
			binding.rvOrders.visibility = View.GONE
			binding.tvNoOrders.visibility = View.VISIBLE
		}
	}

	fun onUserOrdersLoadFailure() {
		hideProgressDialog()
		Toast.makeText(requireContext(), "An error occurred during adding the product to the cart", Toast.LENGTH_SHORT).show()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.orders_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_settings -> {
				startActivity(Intent(activity, SettingsActivity::class.java))
				activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
				return true
			}
			R.id.action_cart -> {
				startActivity(Intent(activity, CartActivity::class.java))
				activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}