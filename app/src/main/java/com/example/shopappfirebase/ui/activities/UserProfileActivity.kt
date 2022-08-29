package com.example.shopappfirebase.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityUserProfileBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {

	private var binding : ActivityUserProfileBinding? = null
	private lateinit var getResultLauncher : ActivityResultLauncher<Intent>

	private lateinit var userDetails : User
	private var imageURI : Uri? = null // location on device
	private var imageURL : String? = null // location in firebase storage

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityUserProfileBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		@Suppress("DEPRECATION")
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.insetsController?.hide(WindowInsets.Type.statusBars())
		} else {
			window.setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			)
		}

		if (intent.hasExtra(Constants.USER_DETAILS)) {
			userDetails = intent.getParcelableExtra(Constants.USER_DETAILS)!!
		}

		if (intent.hasExtra("prevActivity") && intent.getStringExtra("prevActivity") == "settingsActivity") {
			setupActionBar()
			binding?.tvSkip?.visibility = View.INVISIBLE
			binding?.tvTitle?.text = "Edit profile"
		} else {
			binding?.tvTitle?.text = "Complete profile"
			binding?.tvSkip?.setOnClickListener {
				startActivity(Intent(this, DashboardActivity::class.java))
				finish()
			}
		}

		setInputFields()
		setImagePickerResultLauncher()

		binding?.ivUserImage?.setOnClickListener {
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

		binding?.btnSave?.setOnClickListener {
			if (validateForm()) {
				showProgressDialog()
				updateUserProfile()
			}
		}
	}

	fun onUserProfileUpdateSuccess(message: String) {
		Toast.makeText(
			this,
			message,
			Toast.LENGTH_SHORT
		).show()

		if (intent.hasExtra("prevActivity") && intent.getStringExtra("prevActivity") == "settingsActivity") {
			onBackPressed()
			finish()
		} else {
			startActivity(Intent(this, DashboardActivity::class.java))
			finish()
		}
	}

	fun onImageUploadSuccess(uploadedImageURL: String) {
		imageURL = uploadedImageURL
		val userHashMap = HashMap<String, Any>()
		userHashMap[Constants.DATABASE_PROFILE_IMAGE_KEY] = imageURL!!
		FirestoreClass().updateUser(this, userHashMap)
	}

	private fun updateUserProfile() {
		val userHashMap = HashMap<String, Any>()

		userHashMap[Constants.DATABASE_FIRST_NAME_KEY] = binding?.etFirstName?.text.toString()
		userHashMap[Constants.DATABASE_LAST_NAME_KEY] = binding?.etLastName?.text.toString()

		userHashMap[Constants.DATABASE_MOBILE_KEY] = binding?.etPhoneNumber?.text.toString().trim(' ')

		when {
			binding?.rbMale?.isChecked!! -> {
				userHashMap[Constants.DATABASE_GENDER_KEY] = Constants.GENDER_MALE
			}
			binding?.rbFemale?.isChecked!! -> {
				userHashMap[Constants.DATABASE_GENDER_KEY] = Constants.GENDER_FEMALE
			}
			else -> {
				userHashMap[Constants.DATABASE_GENDER_KEY] = Constants.GENDER_HIDDEN
			}
		}

		if (imageURL != null) {
			userHashMap[Constants.DATABASE_PROFILE_IMAGE_KEY] = imageURL!!
		}

		userHashMap[Constants.DATABASE_PROFILE_COMPLETED_KEY] = true
		FirestoreClass().updateUser(this, userHashMap)
	}

	private fun setInputFields() {
		if (userDetails.image.isNotEmpty()) {
			GlideLoader(this).loadPicture(Uri.parse(userDetails.image), binding?.ivUserImage!!, "centerCrop")
		}

		binding?.etFirstName?.setText(userDetails.firstName)

		binding?.etLastName?.setText(userDetails.lastName)

		binding?.etEmail?.setText(userDetails.email)
		binding?.etEmail?.isEnabled = false

		binding?.etPhoneNumber?.setText(userDetails.phoneNumber)

		if (userDetails.gender == "" || userDetails.gender == Constants.GENDER_HIDDEN) {
			binding?.rbHidden?.id?.let { binding?.rgGender?.check(it) }
		}
		else if (userDetails.gender == Constants.GENDER_FEMALE) {
			binding?.rbFemale?.id?.let { binding?.rgGender?.check(it) }
		}
		else {
			binding?.rbMale?.id?.let { binding?.rgGender?.check(it) }
		}
	}

	private fun validateForm() : Boolean {
		return when {
			TextUtils.isEmpty(binding?.etFirstName?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
				false
			}
			TextUtils.isEmpty(binding?.etLastName?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
				false
			}
			else -> {
				true
			}
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
						showProgressDialog()
						FirestoreClass().uploadImage(this, imageURI!!, Constants.DATABASE_PROFILE_IMAGE_KEY)
						GlideLoader(this).loadPicture(imageURI!!, binding?.ivUserImage!!, "centerCrop")
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

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == Constants.READ_EXTERNAL_STORAGE_REQUEST_CODE) {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				getResultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
			} else {
				Toast.makeText(
					this,
					"Permission for reading external storage not granted. You can change it in the app settings.",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.tbCompleteProfile)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.tbCompleteProfile?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		binding = null
	}
}