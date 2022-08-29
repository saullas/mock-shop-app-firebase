package com.example.shopappfirebase.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.FragmentProductsBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.ui.activities.AddProductActivity
import com.example.shopappfirebase.ui.activities.EditProductActivity
import com.example.shopappfirebase.ui.adapters.ProductListAdapter
import com.example.shopappfirebase.utils.Constants

class ProductFragment : BaseFragment() {

	private var _binding: FragmentProductsBinding? = null

	// This property is only valid between onCreateView and
	// onDestroyView.
	private val binding get() = _binding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentProductsBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onResume() {
		super.onResume()
		showProgressDialog()
		FirestoreClass().getUserProducts(this)
	}

	fun onUserProductsLoadSuccess(userProducts: ArrayList<Product>) {
		hideProgressDialog()

		if (userProducts.isNotEmpty()) {
			binding.tvNoProducts.visibility = View.GONE
			binding.rvProducts.visibility = View.VISIBLE

			val adapterUserProducts = ProductListAdapter(requireActivity(), userProducts, this)
			binding.rvProducts.layoutManager = LinearLayoutManager(activity)
			binding.rvProducts.setHasFixedSize(true)
			binding.rvProducts.adapter = adapterUserProducts
		} else {
			binding.tvNoProducts.visibility = View.VISIBLE
			binding.rvProducts.visibility = View.GONE
		}
	}

	fun onUserProductsLoadFailure() {
		hideProgressDialog()
		Toast.makeText(
			activity,
			"An error occured while fetching your products.",
			Toast.LENGTH_SHORT
		).show()
	}

	fun deleteProduct(productId: String) {
		showProgressDialog()
		FirestoreClass().deleteProduct(this, productId)
	}

	fun editProduct(product: Product) {
		val intent = Intent(activity, EditProductActivity::class.java)
		intent.putExtra(Constants.INTENT_EXTRA_PRODUCT_KEY, product)
		startActivity(intent)
	}

	fun showDeleteProductAlertDialog(productId: String) {
		val builder = AlertDialog.Builder(requireActivity())
		builder.setTitle("Delete product")
		builder.setMessage("Are you sure you want to delete this product?")
//		builder.setIcon()
		builder.setPositiveButton("Yes") { dialogInterface, _ ->
			dialogInterface.dismiss()
			deleteProduct(productId)
		}
		builder.setNegativeButton("No") { dialogInterface, _ ->
			dialogInterface.dismiss()
		}
		val alertDialog = builder.create()
		alertDialog.show()
	}

	fun onUserProductsDeleteSuccess() {
		Toast.makeText(
			activity,
			"Product deleted successfully.",
			Toast.LENGTH_SHORT
		).show()

		// Refresh page after deletion
		FirestoreClass().getUserProducts(this)
	}

	fun onUserProductsDeleteFailure() {
		hideProgressDialog()
		Toast.makeText(
			activity,
			"An error occured while deleting your product.",
			Toast.LENGTH_SHORT
		).show()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.add_product_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.actionAddProduct -> {
				startActivity(Intent(activity, AddProductActivity::class.java))
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