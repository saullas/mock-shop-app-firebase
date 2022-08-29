package com.example.shopappfirebase.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.shopappfirebase.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

	private var doubleBackPressToExitPressedOnce = false
	private lateinit var progressDialog: Dialog

	fun showSnackBar(message: String, errorMessage: Boolean) {
		val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
		val snackbarView = snackbar.view

		if (errorMessage) {
			snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarError))
		} else {
			snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSnackBarSuccess))
		}

		snackbar.show()
	}

	fun showProgressDialog() {
		progressDialog = Dialog(this)
		if (!progressDialog.isShowing) {
			progressDialog.setContentView(R.layout.progress_dialog)
			progressDialog.setCancelable(false)
			progressDialog.setCanceledOnTouchOutside(false)
			progressDialog.show()
		}
	}

	fun hideProgressDialog() {
		if (::progressDialog.isInitialized and progressDialog.isShowing) {
			progressDialog.dismiss()
		}
	}

	fun doubleBackToExit() {
		if (doubleBackPressToExitPressedOnce) {
			super.onBackPressed()
			return
		}

		doubleBackPressToExitPressedOnce = true

		Toast.makeText(
			this,
			resources.getString(R.string.please_click_again),
			Toast.LENGTH_SHORT
		).show()

		Handler(Looper.getMainLooper()).postDelayed({
			doubleBackPressToExitPressedOnce = false
		}, 2000)
	}
}