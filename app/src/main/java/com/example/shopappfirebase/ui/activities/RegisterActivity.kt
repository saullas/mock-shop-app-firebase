package com.example.shopappfirebase.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityRegisterBinding
import com.example.shopappfirebase.firestore.FirestoreClass
import com.example.shopappfirebase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {

    var binding : ActivityRegisterBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding?.tvLogin?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        binding?.btnRegister?.setOnClickListener {
            if (validateForm()) {
                showProgressDialog()
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user!!
                    val user = User(
                        firebaseUser.uid,
                        binding?.etFirstName?.text.toString(),
                        binding?.etLastName?.text.toString(),
                        binding?.etEmail?.text.toString(),
                    )
                    FirestoreClass().uploadUser(this@RegisterActivity, user)
                    clearRegisterForm()
                } else {
                    hideProgressDialog()
                    showSnackBar("Something went wrong with authentication. ${task.exception}", true)
                }
            }
    }

    private fun clearRegisterForm() {
        binding?.etFirstName?.text?.clear()
        binding?.etLastName?.text?.clear()
        binding?.etEmail?.text?.clear()
        binding?.etPassword?.text?.clear()
        binding?.etPasswordRepeat?.text?.clear()
        binding?.cbTerms?.isChecked = false
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
            TextUtils.isEmpty(binding?.etEmail?.text.toString().trim(' ')) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding?.etPassword?.text.toString().trim(' ')) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(binding?.etPasswordRepeat?.text.toString().trim(' ')) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }
            binding?.etPassword?.text.toString() != binding?.etPasswordRepeat?.text?.toString() -> {
                showSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !binding?.cbTerms?.isChecked!! -> {
                showSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun onUserRegisterSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "You were registered successfully.",
            Toast.LENGTH_SHORT
        ).show()

//        val intent = Intent(this, LoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
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