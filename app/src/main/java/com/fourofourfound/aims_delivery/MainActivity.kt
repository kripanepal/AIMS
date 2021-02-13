package com.fourofourfound.aims_delivery

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView :BottomNavigationView
    lateinit var navController : NavController

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)
         bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
         navController = findNavController(R.id.myNavHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{ _, nd: NavDestination, _->
                if(nd.id == R.id.loginFragment)
                {
                    bottomNavigationView.visibility = View.GONE
                }
            else
                {
                    bottomNavigationView.visibility = View.VISIBLE
                }

        }
    }

    //forces user to home page on back press
    override fun onBackPressed() {
        var topDestinations = listOf(R.id.settingsFragment,R.id.ongoingDeliveryFragment,R.id.completedDeliveryFragment)
        if (navController.currentDestination?.id in topDestinations) {
            bottomNavigationView.selectedItemId = R.id.homePage
        } else {
           finish()
        }
    }




}