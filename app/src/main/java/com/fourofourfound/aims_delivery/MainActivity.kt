package com.fourofourfound.aims_delivery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.CustomWorkManager
import com.fourofourfound.aims_delivery.utils.LocationPermissionUtil
import com.fourofourfound.aims_delivery.utils.checkPermission
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    lateinit var noInternetText: TextView
    lateinit var locationPermissionUtil:LocationPermissionUtil

    var permissionsToCheck = listOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        locationPermissionUtil = LocationPermissionUtil(this)
        setUpNavController()
        initializeToolBar()
        var sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        changeInternetConnectionText(sharedViewModel)

    }

    private fun changeInternetConnectionText(sharedViewModel: SharedViewModel) {
        noInternetText = findViewById(R.id.no_internet)
        sharedViewModel.internetConnection
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    noInternetText.visibility = View.VISIBLE
                    return@Observer
                }
                noInternetText.visibility = View.GONE

            })
    }

    private fun initializeToolBar() {
        //Add a toolbar
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration
            .Builder(R.id.homePage, R.id.settingsFragment, R.id.ongoingDeliveryFragment)
            .build()
        setupActionBarWithNavController(this, navController, appBarConfiguration)
    }


    private fun setUpNavController() {
        navController = findNavController(R.id.myNavHostFragment)
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            bottomNavigationView.visibility =
                if (nd.id == R.id.loginFragment) View.GONE else View.VISIBLE
        }
        bottomNavigationView.setupWithNavController(navController)
    }


    //forces user to home page on back press
    override fun onBackPressed() {
        val destinations = listOf(
            R.id.settingsFragment,
            R.id.ongoingDeliveryFragment,
            R.id.completedDeliveryFragment
        )
        if (navController.currentDestination?.id in destinations)
            bottomNavigationView.selectedItemId = R.id.homePage
        else finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return navController.navigateUp()
    }


}
