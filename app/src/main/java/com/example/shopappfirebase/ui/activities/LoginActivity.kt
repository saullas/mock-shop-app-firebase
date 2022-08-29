package com.example.shopappfirebase.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityLoginBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.User
import com.example.shopappfirebase.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {

    var binding : ActivityLoginBinding? = null

    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding?.tvRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
//            finish()
        }

        binding?.tvForgotPassword?.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
//            finish()
        }

        binding?.btnLogin?.setOnClickListener {
            if (validateForm()) {
                showProgressDialog()
                login()
            }
        }
    }

    private fun login() {
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    FirestoreClass().getCurrentUserDetails(this)
                } else {
                    showSnackBar("Something went wrong. ${task.exception}", true)
                }
            }
    }

    private fun validateForm() : Boolean {
        return when {
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim(' ')) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding?.etPassword?.text.toString().trim(' ')) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        val sharedPreferences = getSharedPreferences(
            Constants.MYSHOPAPP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString(
            Constants.LOGGED_IN_USERNAME,
            "${user.firstName} ${user.lastName}"
        )

        editor.apply()

        if (user.profileCompleted) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.USER_DETAILS, user) // We can do that because of Parcelable
            intent.putExtra("prevActivity", "loginActivity")
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}