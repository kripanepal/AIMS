package com.fourofourfound.aims_delivery

import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.amitshekhar.DebugDB
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.delivery.onGoing.checkDistanceToDestination
import com.fourofourfound.aims_delivery.domain.DestinationLocation
import com.fourofourfound.aims_delivery.domain.GeoCoordinates
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.*
import com.fourofourfound.aimsdelivery.R
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


/**
 * Main activity
 * This is the main activity of the application
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {

    /**
     * Bottom navigation view
     * The bottom navigation view of the application
     */
    lateinit var bottomNavigationView: BottomNavigationView

    /**
     * Nav controller
     * The nav controller that controls the navigation
     * */
    private lateinit var navController: NavController

    /**
     * Location permission util
     * The class that is responsible for checking and managing background
     * location access to the application
     */
    lateinit var locationPermissionUtil: BackgroundLocationPermissionUtil

    /**
     * Shared view model
     * The view model that is shared within the application
     */
    lateinit var sharedViewModel: SharedViewModel

    /**
     * Delivery status view model
     * The view model that holds the information about current delivery status
     */
    lateinit var deliveryStatusViewModel: DeliveryStatusViewModel

    /**
     * Current nav controller
     * The nav controller that is active within one of the three tabs of
     * bottom navigation
     */
    private var currentNavController: LiveData<NavController>? = null

    /**
     * Dialog
     * The loading animation dialog that is shown within the application
     */
    lateinit var dialog: AlertDialog

    var timer = Handler(Looper.getMainLooper())

    /**
     * On create
     * This method initializes the activity
     * @param savedInstanceState called when activity is starting
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Aims_delivery)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        locationPermissionUtil = BackgroundLocationPermissionUtil(this)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        deliveryStatusViewModel = ViewModelProvider(this).get(DeliveryStatusViewModel::class.java)
        changeInternetConnectionText()
        if (savedInstanceState == null) setupBottomNavigationBar()
        initializeToolBar()
        dialog = showLoadingOverLay(this)
        Thread.UncaughtExceptionHandler { _, _ -> }


        Log.d("DatabaseDebug", DebugDB.getAddressLog())
    }

    /**
     * Observe loading
     * This methods observes the live data from shared view model and displays
     * loading animation accordingly
     */
    private fun observeLoading() {
        sharedViewModel.loading.observe(this) {

                timer.removeCallbacksAndMessages(null)
                if (it) {
                    dialog.show()
                    timer.postDelayed({
                        try {
                        if(dialog.isShowing)
                            dialog.cancel()  }catch (e:Exception){}
                    }, 7000)
                } else dialog.cancel()


        }
    }

    /**
     * On post create
     * This method is called after the activity is created
     * @param savedInstanceState called when activity is started
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        observeLoading()
        trackTruck()

    }

    /**
     * Register broad cast receiver
     * This method registers for a broadcast receiver to listen for internet connection change.
     */
    private fun registerBroadCastReceiver() {
        if(!sharedViewModel.broadCastReceiverInitialized)
        {
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            this.registerReceiver(sharedViewModel.broadCastReceiver,  intentFilter)
            sharedViewModel.broadCastReceiverInitialized = true
        }
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
            navController.addOnDestinationChangedListener { _, destination, _ ->
                var noActionBar = listOf(R.id.loginFragment, R.id.navigationFragment)
                var noBottomNavigation = listOf(R.id.loginFragment, R.id.deliveryCompletionFragment)
                if (destination.id in noActionBar) hideActionBar(this)
                else showActionBar(this)
                if (destination.id in noBottomNavigation) hideBottomNavigation(this)
                else if(destination.id!= R.id.homePage) showBottomNavigation(this)
            }
            setupActionBarWithNavController(it)
        })
        bottomNavigationView.setOnNavigationItemReselectedListener {}
        currentNavController = controller
    }

    /**
     * On support navigate up
     * This method navigates back in the context of current nav controller
     * @return true if nav controller can navigate up, false otherwise
     */
    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    /**
     * Change internet connection text
     * This method displays the no internet connection text
     * if there is no internet.
     */
    private fun changeInternetConnectionText() {
        val noInternetText: TextView = findViewById(R.id.no_internet)
        sharedViewModel.internetConnection
            .observe(this, Observer { isConnected ->
                if (!isConnected) {
                    noInternetText.visibility = View.VISIBLE
                    return@Observer
                } else noInternetText.visibility = View.GONE
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (currentNavController?.value?.currentDestination?.id != R.id.loginFragment) {
            locationPermissionUtil.onPermissionSelected()
        }
    }

    /**
     * On destroy
     * This methods stops the receiving connection broadcast when
     * application close
     */
    override fun onPause() {
        super.onPause()

        dialog.cancel()
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val broadCastReceiverOn = sharedPref.getBoolean("broadCastReceiverOn", false)

        if (broadCastReceiverOn) {
            with(sharedPref.edit()) {
                try {
                    this@MainActivity.unregisterReceiver(sharedViewModel.broadCastReceiver)
                    putBoolean("broadCastReceiverOn", false)
                    apply()
                } catch (e: Exception) {
                    Log.i("NETWORK-CALL", e.message.toString())
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        registerBroadCastReceiver()
    }

    /**
     * On back pressed
     * This method takes user to home screen when back button is pressed from
     * any other top level screen and exits the app if pressed from homepage
     */
    override fun onBackPressed() {
        var topLevelId = listOf(
            R.id.homePage,
            R.id.ongoingDeliveryFragment,
            R.id.settingsFragment
        )
        if ((currentNavController?.value?.currentDestination?.id) in topLevelId) super.onBackPressed()
        else if (currentNavController?.value?.currentDestination?.id == R.id.loginFragment) finish()
        else navController.navigateUp()


    }

    /**
     * Track truck
     * This method tracks the live location of the truck
     */
    private fun trackTruck() {
        deliveryStatusViewModel = ViewModelProvider(this).get(DeliveryStatusViewModel::class.java)
        val locationRepository = deliveryStatusViewModel.locationRepository
        try {
            locationRepository.coordinates.observe(this@MainActivity)
            { currentCoordinates ->
                if (currentCoordinates.latitude != 0.00 && currentCoordinates.longitude != 0.00) {
                    var headingTo = sharedViewModel.selectedSourceOrSite.value?.location
                    if (headingTo != null) {
                        trackDestinationApproaching(headingTo, currentCoordinates)
                        trackDestinationLeaving(currentCoordinates)
                    }
                }
            }
        } catch (e: SecurityException) {
        }
    }

    /**
     * Track destination approaching
     * This method keeps track of the truck location and sends the
     * status update to aims dispatcher and saves the information if update fails
     * @param headingTo the current destination
     * @param currentCoordinates the current coordinates of the phone
     */
    private fun trackDestinationApproaching(
        headingTo: DestinationLocation,
        currentCoordinates: GeoCoordinates
    ) {
        val destination = GeoCoordinates(
            headingTo.latitude, headingTo.longitude
        )
        val distance = checkDistanceToDestination(currentCoordinates, destination)
        if (distance < 500 && !deliveryStatusViewModel.destinationApproachingShown
        ) {
            val statusCodeToGet =
                if (sharedViewModel.selectedSourceOrSite.value!!.wayPointTypeDescription == "Source") StatusMessageEnum.ARRIVESRC else StatusMessageEnum.ARRIVESITE
            val toPut = DatabaseStatusPut(
                sharedViewModel.driver!!.code.trim(),
                sharedViewModel.selectedTrip.value!!.tripId,
                statusCodeToGet.code,
                statusCodeToGet.message,
                getDate(Calendar.getInstance())
            )
            DeliveryStatusViewModel.sendStatusUpdate(toPut, getDatabaseForDriver(this))
            deliveryStatusViewModel.destinationApproachingShown = true
            deliveryStatusViewModel.destinationLeavingShown = false
        }
    }

    /**
     * Track destination leaving
     * This method keeps track of the truck location and sends the
     * status update to aims dispatcher and saves the information if update fails
     * @param currentCoordinates the current coordinates of the phone
     */
    private fun trackDestinationLeaving(currentCoordinates: GeoCoordinates) {
        deliveryStatusViewModel.previousDestination?.apply {
            val oldDestination = GeoCoordinates(this.location.latitude, this.location.longitude)
            var distance = checkDistanceToDestination(
                currentCoordinates,
                oldDestination
            )
            if (distance > 500 && !deliveryStatusViewModel.destinationLeavingShown
            ) {
                val statusCodeToGet =
                    if (deliveryStatusViewModel.previousDestination!!.wayPointTypeDescription == "Source") StatusMessageEnum.LEAVESRC else StatusMessageEnum.LEAVESITE

                val toPut = DatabaseStatusPut(
                    sharedViewModel.driver!!.code.trim(),
                    sharedViewModel.selectedTrip.value!!.tripId,
                    statusCodeToGet.code,
                    statusCodeToGet.message,
                    getDate(Calendar.getInstance())
                )
                DeliveryStatusViewModel.sendStatusUpdate(
                    toPut,
                    getDatabaseForDriver(this@MainActivity)
                )
                deliveryStatusViewModel.destinationLeavingShown = true
                deliveryStatusViewModel.destinationApproachingShown = false
                deliveryStatusViewModel.previousDestination = null
            }
        }
    }

    /**
     * Get database
     * This method gets the database for the current driver
     */
    fun getDatabase() {
        deliveryStatusViewModel.getUpdatedDatabase()
    }

    /**
     * Show loading over lay
     * This method defines how the loading animation is shown within the application
     * @param context the context of the application
     * @return the dialog for loading animation
     */
    private fun showLoadingOverLay(context: Context): AlertDialog {
        var builder = AlertDialog.Builder(context);
        val progressBar = ProgressBar(context)
        val cube = CubeGrid()
        cube.color = context.getColor(R.color.Aims_Orange)
        progressBar.indeterminateDrawable = cube
        progressBar.setPadding(0, 50, 0, 50)
        builder.setView(progressBar);
        return builder.create().apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
