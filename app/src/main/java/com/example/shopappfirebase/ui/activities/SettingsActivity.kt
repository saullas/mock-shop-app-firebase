package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivitySettingsBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.utils.Constants
import com.example.shopappfirebase.utils.GlideLoader
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity : BaseActivity() {

	private var binding : ActivitySettingsBinding? = null
	private var auth = Firebase.auth
	private lateinit var userDetails: User

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivitySettingsBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()
		getUserDetails()

		binding?.tvEdit?.setOnClickListener {
			val intent = Intent(this, UserProfileActivity::class.java)
			intent.putExtra(Constants.USER_DETAILS, userDetails)
			intent.putExtra("prevActivity", "settingsActivity")
			startActivity(intent)
		}

		binding?.llAddress?.setOnClickListener {
			val intent = Intent(this, AddressListActivity::class.java)
			startActivity(intent)
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
		}

		binding?.btnLogout?.setOnClickListener {
			auth.signOut()

			Toast.makeText(
				this,
				"You were signed out successfully!",
				Toast.LENGTH_SHORT
			).show()

			val intent = Intent(this, LoginActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
			startActivity(intent)
			finish()
		}
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarSettingsActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarSettingsActivity?.setNavigationOnClickListener {
			onBackPressed()
		}
	}

	private fun getUserDetails() {
		showProgressDialog()
		FirestoreClass().getCurrentUserDetails(this)
		hideProgressDialog()
	}

	fun onUserDetailsLoadedSuccessfuly(user: User) {
		userDetails = user

		GlideLoader(this).loadPicture(Uri.parse(user.image), binding?.ivUserPhoto!!, "centerCrop")

		binding?.tvName?.text = "${user.firstName} ${user.lastName}"
		binding?.tvGender?.text = user.gender
		binding?.tvEmail?.text = user.email
		binding?.tvMobileNumber?.text = user.phoneNumber
	}

	override fun onResume() {
		super.onResume()
		getUserDetails()
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