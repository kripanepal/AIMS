package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.NavigationFragmentBinding
import com.here.android.mpa.common.*
import com.here.android.mpa.guidance.LaneInformation
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.NavigationManager.LaneInformationListener
import com.here.android.mpa.guidance.NavigationManager.NewInstructionEventListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.prefetcher.MapDataPrefetcher
import com.here.android.mpa.prefetcher.MapDataPrefetcher.Listener.PrefetchStatus
import com.here.android.mpa.routing.*
import com.here.android.mpa.routing.Route.TrafficPenaltyMode
import java.lang.ref.WeakReference
import kotlin.properties.Delegates


class NavigationFragment : Fragment() {


    private lateinit var viewModel: NavigationViewModel
    lateinit var binding: NavigationFragmentBinding
    lateinit var locationManager: LocationManager
    var id: Long = -1

    // map embedded in the map fragment
    lateinit var map: Map

    // map fragment embedded in this activity
    lateinit var mapFragment: AndroidXMapFragment
    private var currentLatitude by Delegates.notNull<Double>()
    var currentLongitude by Delegates.notNull<Double>()


    lateinit var navigationManager: NavigationManager
    lateinit var geoBoundingBox: GeoBoundingBox
    var route: Route? = null

    lateinit var mPrefs: SharedPreferences


    private var fetchingDataInProgress = false

    private var foregroundServiceStarted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.navigation_fragment, container, false)
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        mPrefs = requireActivity().getPreferences(MODE_PRIVATE)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.mapRecenterBtn.setOnClickListener { recenter() }
        binding.destinationReached.setOnClickListener { destinationReached() }

        initializeMap()
        return binding.root
    }


    @SuppressLint("MissingPermission")
    private fun initializeMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment
        mapFragment.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                if (route !== null) {
                    mapFragment.onResume()
                    navigationManager.resume()
                    binding.mapFragmentContainer.visibility = View.VISIBLE
                } else {
                    map = mapFragment.map!!
                    PositioningManager.getInstance().lastKnownPosition.coordinate.apply {
                        currentLatitude = latitude
                        currentLongitude = longitude
                    }

                    map.setCenter(
                        GeoCoordinate(currentLatitude, currentLongitude, 0.0),
                        Map.Animation.NONE
                    )
                    // Set the zoom level to the average between min and max
                    map.zoomLevel = (map.maxZoomLevel + map.minZoomLevel) / 2
                    navigationManager = NavigationManager.getInstance()
                    createRoute()
                }
            } else {
                Log.i("AAAA", error.toString())
            }
        }
    }


    private fun createRoute() {
        val coreRouter = CoreRouter()
        val routePlan = RoutePlan()
        val routeOptions = RouteOptions()
        routeOptions.transportMode = RouteOptions.TransportMode.CAR
        routeOptions.routeType = RouteOptions.Type.SHORTEST

        routeOptions.routeCount = 1
        routePlan.routeOptions = routeOptions


        val startPoint = RouteWaypoint(GeoCoordinate(currentLatitude, currentLongitude))
        val destination = RouteWaypoint(GeoCoordinate(32.52568, -92.04272))

        routePlan.addWaypoint(startPoint)
        routePlan.addWaypoint(destination)
        binding.progressBarContainer.visibility = View.VISIBLE

        coreRouter.calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onProgress(i: Int) {
                    binding.routeProgressBar.progress = i
                }

                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>?,
                    routingError: RoutingError
                ) {

                    if (routingError == RoutingError.NONE) {
                        if (routeResults!![0].route != null) {

                            binding.progressBarContainer.visibility = View.GONE
                            binding.mapFragmentContainer.visibility = View.VISIBLE

                            route = routeResults[0].route
                            val mapRoute = MapRoute(routeResults[0].route)

                            mapRoute.isManeuverNumberVisible = true

                            map.addMapObject(mapRoute)

                            geoBoundingBox = routeResults[0].route.boundingBox!!
                            map.zoomTo(
                                geoBoundingBox,
                                Map.Animation.NONE,
                                Map.MOVE_PRESERVE_TILT.toFloat()
                            )
                            startNavigation()


                        } else {
                            Toast.makeText(
                                context,
                                "Error:route results returned is not valid",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigateUp()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error:route calculation returned error code: $routingError",
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigateUp()
                    }
                }
            })
    }

    private fun recenter() {
        navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.ROADVIEW
        Handler(Looper.getMainLooper()).postDelayed({
            navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.POSITION_ANIMATION
        }, 2000)

    }


    private fun startNavigation() {

        navigationManager.setMap(map)
        mapFragment.positionIndicator?.isVisible = true

        if (navigationManager.runningState !== NavigationManager.NavigationState.RUNNING) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Navigation")
            alertDialogBuilder.setMessage("Choose Mode")
            alertDialogBuilder.setNegativeButton("Navigation") { dialoginterface, i ->
                navigationManager.startNavigation(route!!)
                map.tilt = 70f
                startForegroundService()
            }
            alertDialogBuilder.setPositiveButton("Simulation") { dialoginterface, i ->
                navigationManager.simulate(route!!, 10)
                map.tilt = 70f
                startForegroundService()

            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        } else {
            mapFragment.onResume()
        }
        addListeners()
    }

    private fun addListeners() {
        navigationManager.distanceUnit = NavigationManager.UnitSystem.IMPERIAL_US
        navigationManager.addRerouteListener(WeakReference(rerouteListener))
        navigationManager.addNavigationManagerEventListener(WeakReference(routeCompleteListener))
        MapDataPrefetcher.getInstance().addListener(prefetchListener)
        PositioningManager.getInstance().addListener(WeakReference(positionLister))
        navigationManager.addLaneInformationListener(WeakReference(laneInformationListener))

        navigationManager.addNewInstructionEventListener(WeakReference(instructListener))
        navigationManager.addPositionListener(WeakReference(positionListener))
        setUpVoiceNavigation()

    }


    private fun removeListeners() {
        navigationManager.apply {

            navigationManager.removeRerouteListener(rerouteListener)
            navigationManager.removeNavigationManagerEventListener(routeCompleteListener)
            MapDataPrefetcher.getInstance().removeListener(prefetchListener)
            PositioningManager.getInstance().removeListener(positionLister)
            navigationManager.stop()
            stopForegroundService()
        }


    }

    private var rerouteListener = object : NavigationManager.RerouteListener() {
        override fun onRerouteEnd(p0: RouteResult, p1: RoutingError?) {
            super.onRerouteEnd(p0, p1)
            map.addMapObject(MapRoute(p0.route))
        }
    }

    private var routeCompleteListener =
        object : NavigationManager.NavigationManagerEventListener() {
            override fun onDestinationReached() {
                destinationReached()
                super.onDestinationReached()
            }
        }

    private var prefetchListener: MapDataPrefetcher.Adapter =
        object : MapDataPrefetcher.Adapter() {
            override fun onStatus(requestId: Int, status: PrefetchStatus) {
                if (status != PrefetchStatus.PREFETCH_IN_PROGRESS) {
                    fetchingDataInProgress = false
                }
            }
        }

    private var positionLister = object : PositioningManager.OnPositionChangedListener {
        override fun onPositionUpdated(
            p0: PositioningManager.LocationMethod?,
            p1: GeoPosition?,
            p2: Boolean
        ) {
            if (PositioningManager.getInstance().roadElement == null && !fetchingDataInProgress) {
                val areaAround = p1?.let { GeoBoundingBox(it.coordinate, 500F, 500F) }
                if (areaAround != null) {
                    MapDataPrefetcher.getInstance().fetchMapData(areaAround)
                }
                fetchingDataInProgress = true
            }
            if (p1 != null) {
                if (p1.isValid && p1 is MatchedGeoPosition) {
                    var currentSpeedLimitTransformed = 0
                    val currentSpeed: Double = p1.speed
                    if (p1.roadElement != null) {
                        val currentSpeedLimit: Double = p1.roadElement!!.speedLimit.toDouble()
                        currentSpeedLimitTransformed = meterPerSecToKmPerHour(currentSpeedLimit)
                    }
                    updateCurrentSpeedView(currentSpeed)
                    updateCurrentSpeedLimitView(currentSpeedLimitTransformed)
                } else {

                }
            }
        }

        override fun onPositionFixChanged(
            p0: PositioningManager.LocationMethod?,
            p1: PositioningManager.LocationStatus?
        ) {
        }
    }


    //Task to be done once destination is reached
    private fun destinationReached() {
        removeListeners()
        lifecycleScope.launchWhenResumed {
            findNavController().navigateUp()
        }
    }


    private val laneInformationListener: LaneInformationListener =
        object : LaneInformationListener() {
            override fun onLaneInformation(
                items: List<LaneInformation>,
                roadElement: RoadElement?
            ) {
                Log.i("AAAAAAA", items.toString())
                Log.i("AAAAAAA", roadElement.toString())
            }
        }


    private fun meterPerSecToKmPerHour(speed: Double): Int {
        return (speed * 0.681818).toInt()
    }


    private fun updateCurrentSpeedView(currentSpeed: Double) {
        binding.currentSpeed.text = currentSpeed.toString()
    }

    private fun updateCurrentSpeedLimitView(currentSpeedLimit: Int) {
        val currentSpeedLimitText: String = if (currentSpeedLimit > 0) {
            currentSpeedLimit.toString()
        } else {
            "N/A"
        }
        binding.currentSpeedLimit.text = currentSpeedLimitText

    }


// add application specific logic in each of the callbacks.

    // declare the listeners
    // add application specific logic in each of the callbacks.
    private val instructListener: NewInstructionEventListener =
        object : NewInstructionEventListener() {
            override fun onNewInstructionEvent() {
                // Interpret and present the Maneuver object as it contains
                // turn by turn navigation instructions for the user.
                navigationManager.nextManeuver

            }
        }

    private val positionListener: NavigationManager.PositionListener =
        object : NavigationManager.PositionListener() {
            override fun onPositionUpdated(loc: GeoPosition) {
                // the position we get in this callback can be used
                // to reposition the map and change orientation.
                loc.coordinate
                loc.heading
                loc.speed

                // also remaining time and distance can be
                // fetched from navigation manager
                navigationManager.getTta(TrafficPenaltyMode.DISABLED, true)
                navigationManager.destinationDistance
            }
        }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
    }

    private fun startForegroundService() {
        if (!foregroundServiceStarted) {
            foregroundServiceStarted = true
            val startIntent = Intent(requireContext(), ForegroundService::class.java)
            startIntent.action = ForegroundService.START_ACTION
            requireContext().startService(startIntent)
        }
    }

    private fun stopForegroundService() {
        if (foregroundServiceStarted) {
            foregroundServiceStarted = false
            val stopIntent = Intent(requireContext(), ForegroundService::class.java)
            stopIntent.action = ForegroundService.STOP_ACTION
            requireContext().startService(stopIntent)
        }
    }

}
