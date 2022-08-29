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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityAddProductBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Product
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity() {

	private var binding: ActivityAddProductBinding? = null
	private lateinit var getResultLauncher : ActivityResultLauncher<Intent>

	private var imageURI : Uri? = null // location on device
	private var imageURL : String? = null // location in firebase storage
	private var imageUploadError : Boolean? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityAddProductBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setImagePickerResultLauncher()
		setupActionBar()

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
				uploadProduct()
			}
		}
	}

	private fun uploadProduct() {
		if (imageURI != null) {
			FirestoreClass().uploadImage(this, imageURI!!, Constants.DATABASE_PRODUCT_IMAGE_KEY)
		} else {
			onImageUploadFinish("")
		}
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.tbCart)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.tbCart?.setNavigationOnClickListener {
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

	fun onProductUploadFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"There was an error when uploading your product.",
			Toast.LENGTH_SHORT
		).show()
	}

	fun onProductUploadSuccess() {
		hideProgressDialog()
		if (imageUploadError!!) {
			Toast.makeText(
				this,
				"Product added successfully, but there was an error uploading product image.",
				Toast.LENGTH_SHORT
			).show()
		} else {
			Toast.makeText(
				this,
				"Product added successfully",
				Toast.LENGTH_SHORT
			).show()
		}
		finish()
	}

	fun onImageUploadFinish(imgUrl: String) {
		val username = getSharedPreferences(
			Constants.MYSHOPAPP_PREFERENCES, Context.MODE_PRIVATE)
			.getString(Constants.LOGGED_IN_USERNAME, "")!!

		val product = Product(
			FirestoreClass().getCurrentUserId(),
			username,
			binding?.etName?.text.toString(),
			binding?.etPrice?.text.toString(),
			binding?.etQuantity?.text.toString(),
			binding?.etDescription?.text.toString()
		)

		if (imgUrl.isNotEmpty()) {
			product.image = imgUrl
			imageURL = imgUrl
			imageUploadError = false
		} else {
			imageUploadError = imageURI != null
		}

		FirestoreClass().uploadProduct(this, product)
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}