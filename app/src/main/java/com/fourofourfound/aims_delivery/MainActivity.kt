package com.fourofourfound.aims_delivery

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navController = findNavController(R.id.myNavHostFragment)
        bottomNavigationView.setupWithNavController(navController)


        //prevents reselection of the destination and navigates user back to top destination
        bottomNavigationView.setOnNavigationItemReselectedListener {
            navController.currentDestination?.let {currentDestination ->
                if(currentDestination.id!==it.itemId)
                    navController.navigate(it.itemId)
            }
        }

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            bottomNavigationView.visibility = if (nd.id == R.id.loginFragment) View.GONE else View.VISIBLE
        }
    }

    //forces user to home page on back press
    override fun onBackPressed() {
        var topDestinations = listOf(
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