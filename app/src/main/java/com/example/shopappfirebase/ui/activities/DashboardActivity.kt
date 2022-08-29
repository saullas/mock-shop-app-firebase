package com.example.shopappfirebase.ui.activities

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.shopappfirebase.R
import com.example.shopappfirebase.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {

	private lateinit var binding: ActivityDashboardBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityDashboardBinding.inflate(layoutInflater)
		setContentView(binding.root)

		supportActionBar?.setBackgroundDrawable(
			ContextCompat.getDrawable(
				this,
				R.drawable.app_gradient_color_background
			)
		)

		val navView: BottomNavigationView = binding.navView

		val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.navigation_dashboard, R.id.navigation_products, R.id.navigation_orders
			)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		navView.setupWithNavController(navController)
	}

	override fun onBackPressed() {
		doubleBackToExit()
	}
}