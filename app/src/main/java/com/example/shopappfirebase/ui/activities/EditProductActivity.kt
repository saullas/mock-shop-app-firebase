package com.example.shopappfirebase.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityEditProductBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader
import java.io.IOException

class EditProductActivity : BaseActivity() {

	private var binding: ActivityEditProductBinding? = null
	private lateinit var getResultLauncher : ActivityResultLauncher<Intent>

	private var imageURI : Uri? = null // location on device
	private var imageURL : String? = null // location in firebase storage
	private var imageUploadError : Boolean? = null
	private var productToEdit: Product? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityEditProductBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setImagePickerResultLauncher()
		setupActionBar()

		if (intent.hasExtra(Constants.INTENT_EXTRA_PRODUCT_KEY)) {
			productToEdit = intent.getParcelableExtra(Constants.INTENT_EXTRA_PRODUCT_KEY)
			setFields()
		}

		binding?.ivAddProductPhoto?.setOnClickListener {
			if (ContextCompat.checkSelfPermission(
					this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				getResultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
			} else {
				ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
					Constants.READ_EXTERNAL_STORAGE_REQUEST_CODE
				)
			}
		}

		binding?.btnSubmit?.setOnClickListener {
			if (validateForm()) {
				showProgressDialog()
				updateProduct()
			}
		}
	}

	private fun setFields() {
		if (productToEdit?.image!!.isNotEmpty()) {
			binding?.ivNoPhoto?.visibility = View.GONE
			GlideLoader(this).loadProductPicture(productToEdit?.image!!, binding?.ivProductImage!!, "fitCenter")
		} else {
			binding?.ivNoPhoto?.visibility = View.VISIBLE
		}
		binding?.etName?.setText(productToEdit?.name)
		binding?.etPrice?.setText(productToEdit?.price)
		binding?.etQuantity?.setText(productToEdit?.stockQuantity)
		binding?.etDescription?.setText(productToEdit?.description)
	}

	private fun updateProduct() {
		if (imageURI != null) {
			FirestoreClass().uploadImage(this, imageURI!!, Constants.DATABASE_PRODUCT_IMAGE_KEY)
		} else {
			onImageUploadFinish("")
		}
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.tbEditProduct)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.tbEditProduct?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	private fun setImagePickerResultLauncher() {
		getResultLauncher = registerForActivityResult(
			ActivityResultContracts.StartActivityForResult()
		) { result ->
			if (result.resultCode == Activity.RESULT_OK) {
				if (result.data != null) {
					try {
						imageURI = result.data!!.data!!
						GlideLoader(this).loadPicture(imageURI!!, binding?.ivProductImage!!, "fitCenter")
					} catch (e: IOException) {
						e.printStackTrace()
						Toast.makeText(
							this,
							"There was an error. The image couldn't be selected",
							Toast.LENGTH_SHORT
						).show()
					}
				}
			}
		}
	}

	private fun validateForm() : Boolean {
		return when {
			TextUtils.isEmpty(binding?.etName?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_title), true)
				false
			}
			TextUtils.isEmpty(binding?.etPrice?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_price), true)
				false
			}
			TextUtils.isEmpty(binding?.etQuantity?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_quantity), true)
				false
			}
			else -> {
				true
			}
		}
	}

	fun onProductUpdateFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"An error occurred during update of your product.",
			Toast.LENGTH_SHORT
		).show()
	}

	fun onProductUpdateSuccess() {
		hideProgressDialog()
		if (imageUploadError!!) {
			Toast.makeText(
				this,
				"Product updated successfully, but there was an error uploading product image.",
				Toast.LENGTH_SHORT
			).show()
		} else {
			Toast.makeText(
				this,
				"Product updated successfully",
				Toast.LENGTH_SHORT
			).show()
		}
		finish()
	}

	fun onImageUploadFinish(imgUrl: String) {
		val username = getSharedPreferences(
			Constants.MYSHOPAPP_PREFERENCES, Context.MODE_PRIVATE)
			.getString(Constants.LOGGED_IN_USERNAME, "")!!

		val productHashMap = HashMap<String, Any>()
		productHashMap[Constants.DATABASE_PRODUCT_NAME_KEY] = binding?.etName?.text.toString()
		productHashMap[Constants.DATABASE_PRODUCT_PRICE_KEY] = binding?.etPrice?.text.toString()
		productHashMap[Constants.DATABASE_PRODUCT_QUANTITY_KEY] = binding?.etQuantity?.text.toString()
		productHashMap[Constants.DATABASE_PRODUCT_DESCRIPTION_KEY] = binding?.etDescription?.text.toString()
		productHashMap[Constants.DATABASE_PRODUCT_USERNAME_KEY] = username

		if (imgUrl.isNotEmpty()) {
			productHashMap[Constants.DATABASE_PRODUCT_IMAGE_KEY] = imgUrl
			imageURL = imgUrl
			imageUploadError = false
		} else {
			imageUploadError = imageURI != null
		}

		FirestoreClass().updateProduct(this, productToEdit?.id!!, productHashMap)
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}