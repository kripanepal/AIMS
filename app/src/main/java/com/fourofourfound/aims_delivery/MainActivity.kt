package com.fourofourfound.aims_delivery

import android.os.Bundle
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
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    lateinit var noInternetText: TextView
    lateinit var locationPermissionUtil: BackgroundLocationPermissionUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        locationPermissionUtil = BackgroundLocationPermissionUtil(this)
        setUpNavController()
        initializeToolBar()
        val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
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
            .Builder(
                R.id.homePage,
                R.id.settingsFragment,
                R.id.ongoingDeliveryFragment,
                R.id.loginFragment
            )
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (navController.currentDestination?.id != R.id.loginFragment) {
            locationPermissionUtil.onPermissionSelected()
        }
    }


    override fun onStart() {
        super.onStart()
        if (navController.currentDestination?.id != R.id.loginFragment)
            locationPermissionUtil.checkPermissionsOnStart()
    }


}
