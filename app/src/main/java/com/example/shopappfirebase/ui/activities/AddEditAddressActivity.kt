package com.example.shopappfirebase.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityAddAddressBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.Address
import com.example.shopappfirebase.utils.Constants

class AddEditAddressActivity : BaseActivity() {

	private var binding : ActivityAddAddressBinding? = null
	private var address : Address? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityAddAddressBinding.inflate(layoutInflater)
		setContentView(binding?.root)

		setupActionBar()

		if (intent.hasExtra(Constants.INTENT_EXTRA_ADDRESS_KEY)) {
			address = intent.getParcelableExtra(Constants.INTENT_EXTRA_ADDRESS_KEY)
			setFields(address!!)
		}

		binding?.btnSubmitAddress?.setOnClickListener {
			if (validateForm()) {
				showProgressDialog()
				uploadAddress()
			}
		}
	}

	private fun uploadAddress() {
		val addressType : String = when {
			binding?.rbHome!!.isChecked -> {
				Constants.DATABASE_ADDRESS_HOME_ADDRESS
			}
			binding?.rbOffice!!.isChecked -> {
				Constants.DATABASE_ADDRESS_OFFICE_ADDRESS
			}
			else -> {
				Constants.DATABASE_ADDRESS_OTHER_ADDRESS
			}
		}

		val newAddress = Address(
			FirestoreClass().getCurrentUserId(),
			binding?.etFullName?.text.toString(),
			binding?.etPhoneNumber?.text.toString(),
			binding?.etAddress?.text.toString(),
			binding?.etZipCode?.text.toString(),
			binding?.etAdditionalNote?.text.toString(),
			addressType
		)

		if (intent.hasExtra(Constants.INTENT_EXTRA_ADDRESS_KEY)) {
			newAddress.id = address!!.id
			FirestoreClass().updateAddress(this, newAddress)
		} else {
			FirestoreClass().uploadAddress(this, newAddress)
		}
	}

	private fun setFields(address: Address) {
		binding?.etFullName?.setText(address.name)
		binding?.etPhoneNumber?.setText(address.phoneNumber)
		binding?.etAddress?.setText(address.address)
		binding?.etZipCode?.setText(address.zipCode)
		binding?.etAdditionalNote?.setText(address.additionalNote)

		when (address.type) {
			Constants.DATABASE_ADDRESS_HOME_ADDRESS -> {
				binding?.rbHome!!.isChecked = true
			}
			Constants.DATABASE_ADDRESS_OFFICE_ADDRESS -> {
				binding?.rbOffice!!.isChecked = true
			}
			else -> {
				binding?.rbOther!!.isChecked = true
			}
		}
	}

	fun onAddressUploadSuccess() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"Address uploaded successfully",
			Toast.LENGTH_SHORT
		).show()
		clearForm()
		onBackPressed()
	}

	fun onAddressUploadFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"An error occurred during uploading the address",
			Toast.LENGTH_SHORT
		).show()
	}

	fun onAddressUpdateSuccess() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"Address updated successfully",
			Toast.LENGTH_SHORT
		).show()
		clearForm()
		onBackPressed()
	}

	fun onAddressUpdateFailure() {
		hideProgressDialog()
		Toast.makeText(
			this,
			"An error occurred during updating the address",
			Toast.LENGTH_SHORT
		).show()
	}

	private fun validateForm() : Boolean {
		return when {
			TextUtils.isEmpty(binding?.etFullName?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_full_address_name), true)
				false
			}
			TextUtils.isEmpty(binding?.etPhoneNumber?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_phone), true)
				false
			}
			TextUtils.isEmpty(binding?.etAddress?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_address), true)
				false
			}
			TextUtils.isEmpty(binding?.etZipCode?.text.toString().trim(' ')) -> {
				showSnackBar(resources.getString(R.string.err_msg_enter_zip_code), true)
				false
			}
			else -> {
				true
			}
		}
	}

	private fun clearForm() {
		binding?.etFullName?.text?.clear()
		binding?.etZipCode?.text?.clear()
		binding?.etAddress?.text?.clear()
		binding?.etAdditionalNote?.text?.clear()
		binding?.etPhoneNumber?.text?.clear()
	}

	private fun setupActionBar() {
		setSupportActionBar(binding?.toolbarAddEditAddressActivity)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
		binding?.toolbarAddEditAddressActivity?.setNavigationOnClickListener {
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