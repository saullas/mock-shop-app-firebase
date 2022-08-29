package com.example.shopappfirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.FragmentDashboardBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.ui.activities.CartActivity
import com.example.shopappfirebase.ui.activities.SettingsActivity
import com.example.shopappfirebase.ui.adapters.DashboardListAdapter
import java.util.ArrayList

class DashboardFragment : BaseFragment() {

	private var _binding: FragmentDashboardBinding? = null

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
		showProgressDialog()
		FirestoreClass().getAllProducts(this)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentDashboardBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.dashboard_menu, menu)
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

	fun onUserProductsLoadSuccess(products: ArrayList<Product>) {
		hideProgressDialog()
		if (products.isNotEmpty()) {
			binding.tvNoItems.visibility = View.GONE
			binding.rvProducts.visibility = View.VISIBLE

			val adapterDashboardProducts = DashboardListAdapter(requireActivity(), products)
			binding.rvProducts.layoutManager = GridLayoutManager(activity, 2)
			binding.rvProducts.setHasFixedSize(true)
			binding.rvProducts.adapter = adapterDashboardProducts
		} else {
			binding.tvNoItems.visibility = View.VISIBLE
			binding.rvProducts.visibility = View.GONE
		}
	}

	fun onUserProductsLoadFailure() {
		hideProgressDialog()
		Toast.makeText(
			activity,
			"An error occured while fetching products.",
			Toast.LENGTH_SHORT
		).show()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}