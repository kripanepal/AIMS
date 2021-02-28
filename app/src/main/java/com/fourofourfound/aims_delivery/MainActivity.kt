package com.fourofourfound.aims_delivery

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navController: NavController
    lateinit var noInternetText: TextView
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navController = findNavController(R.id.myNavHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        noInternetText = findViewById(R.id.no_internet)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.loginFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        sharedViewModel.internetConnection
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    noInternetText.visibility = View.VISIBLE
                    return@Observer
                }
                noInternetText.visibility = View.GONE

            })

        //Add a toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

    }

    //forces user to home page on back press
    override fun onBackPressed() {
        val topDestinations = listOf(
            R.id.settingsFragment,
            R.id.ongoingDeliveryFragment,
            R.id.completedDeliveryFragment
        )

        if (navController.currentDestination?.id in topDestinations) {
            bottomNavigationView.selectedItemId = R.id.homePage
        } else {
            finish()
        }
    }


}





