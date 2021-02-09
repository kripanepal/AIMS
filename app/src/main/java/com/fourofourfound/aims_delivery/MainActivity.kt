package com.fourofourfound.aims_delivery

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fourofourfound.encrypted_preferences.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = findNavController(R.id.myNavHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{ _, nd:NavDestination, _->
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




}