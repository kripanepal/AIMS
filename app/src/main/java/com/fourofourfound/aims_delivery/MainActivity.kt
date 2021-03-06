package com.fourofourfound.aims_delivery

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.fourofourfound.aims_delivery.broadcastReceiver.NetworkChangedBroadCastReceiver
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aims_delivery.utils.setupWithNavController
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main activity
 * This is the main activity of the application
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    lateinit var noInternetText: TextView
    lateinit var locationPermissionUtil: BackgroundLocationPermissionUtil
    lateinit var sharedViewModel: SharedViewModel
    private var currentNavController: LiveData<NavController>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        locationPermissionUtil = BackgroundLocationPermissionUtil(this)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        changeInternetConnectionText()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState


        initializeToolBar()

    }

    /**
     * On restore instance state
     * Now that BottomNavigationBar has restored its instance state
     * and its selectedItemId, we can proceed with setting up the
     * BottomNavigationBar with Navigation
     * @param savedInstanceState the values that are saved in the bundle
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        setupBottomNavigationBar()
    }

    /**
     * Setup bottom navigation bar
     * This method sets up navigation bar with top level
     * destination
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.home_navigation,
            R.navigation.delivery_navigation,
            R.navigation.settings_navigation
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.myNavHostFragment,
            intent = intent
        )

        controller.observe(this, Observer {
            navController = it
            setupActionBarWithNavController(it)
        })


        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    /**
     * Change internet connection text
     * This method displays the no internet connection text
     * if there is no internet.
     */
    private fun changeInternetConnectionText() {
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

    /**
     * Initialize tool bar
     * This method initializes the custom toolbar
     */
    private fun initializeToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
    }

    /**
     * On request permissions result
     * TODO This might be replaced with Foreground service
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (currentNavController?.value?.currentDestination?.id != R.id.loginFragment) {
            locationPermissionUtil.onPermissionSelected()
        }
    }

    /**
     * On destroy
     * This methods stops the receiving connection broadcast when
     * application close
     */
    override fun onDestroy() {
        super.onDestroy()
        if (sharedViewModel.isLocationBroadcastReceiverInitialized) {
            try {
                this.unregisterReceiver(NetworkChangedBroadCastReceiver())
            } catch (e: Exception) {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        currentNavController?.value?.navigateUp()
    }

}
