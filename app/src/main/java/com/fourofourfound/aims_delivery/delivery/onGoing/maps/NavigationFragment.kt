package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.NavigationManager.NewInstructionEventListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.prefetcher.MapDataPrefetcher
import com.here.android.mpa.prefetcher.MapDataPrefetcher.Listener.PrefetchStatus
import com.here.android.mpa.routing.*
import java.lang.ref.WeakReference
import kotlin.properties.Delegates


class NavigationFragment : Fragment() {


    private lateinit var viewModel: NavigationViewModel
    lateinit var binding: NavigationFragmentBinding
    lateinit var locationManager: LocationManager
    var voiceId: Long = -1
    lateinit var map: Map
    lateinit var mapFragment: AndroidXMapFragment
    private var currentLatitude by Delegates.notNull<Double>()
    var currentLongitude by Delegates.notNull<Double>()
    lateinit var navigationManager: NavigationManager
    lateinit var geoBoundingBox: GeoBoundingBox
    var route: Route? = null
    private var fetchingDataInProgress = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.navigation_fragment, container, false)
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
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
                    binding.routeProgressBar.isIndeterminate = true
                }

                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>?,
                    routingError: RoutingError
                ) {

                    if (routingError == RoutingError.NONE) {
                        if (routeResults!![0].route != null) {


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

            }
            alertDialogBuilder.setPositiveButton("Simulation") { dialoginterface, i ->
                navigationManager.simulate(route!!, 50)
                map.tilt = 70f

            }
            val alertDialog = alertDialogBuilder.create()
            alertDialogBuilder.setCancelable(false)
            alertDialog.show()


        } else {
            mapFragment.onResume()
        }
        binding.speedInfoContainer.visibility = View.VISIBLE
        binding.destinationReached.visibility = View.VISIBLE
        binding.mapRecenterBtn.visibility = View.VISIBLE
        binding.progressBarContainer.visibility = View.GONE
        addListeners()
    }

    private fun addListeners() {

        navigationManager.distanceUnit = NavigationManager.UnitSystem.IMPERIAL_US
        navigationManager.addRerouteListener(WeakReference(rerouteListener))
        navigationManager.addNavigationManagerEventListener(WeakReference(routeCompleteListener))
        MapDataPrefetcher.getInstance().addListener(prefetchListener)
        PositioningManager.getInstance().addListener(WeakReference(positionLister))
        navigationManager.addNewInstructionEventListener(WeakReference(instructListener))
        setUpVoiceNavigation()

    }


    private fun removeListeners() {
        navigationManager.apply {
            navigationManager.removeRerouteListener(rerouteListener)
            navigationManager.removeNavigationManagerEventListener(routeCompleteListener)
            MapDataPrefetcher.getInstance().removeListener(prefetchListener)
            PositioningManager.getInstance().removeListener(positionLister)
            navigationManager.stop()
        }
    }

    private var rerouteListener = object : NavigationManager.RerouteListener() {
        override fun onRerouteEnd(p0: RouteResult, p1: RoutingError?) {
            super.onRerouteEnd(p0, p1)
            map.removeAllMapObjects()
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
                    var currentSpeedLimit = 0.0
                    val currentSpeed: Double = p1.speed
                    if (p1.roadElement != null) {
                        currentSpeedLimit = p1.roadElement!!.speedLimit.toDouble()

                    }
                    updateSpeedTexts(currentSpeed, currentSpeedLimit)
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


    private fun meterPerSecToMilesPerHour(speed: Double): Int {
        return (speed * 2.23694).toInt()
    }


    private fun updateSpeedTexts(currentSpeed: Double, currentSpeedLimit: Double) {
        val currentSpeedLimitText: String = if (currentSpeedLimit > 0) {
            meterPerSecToMilesPerHour(currentSpeedLimit).toString()
        } else {
            "N/A"
        }
        binding.currentSpeedLimit.text = currentSpeedLimitText
        binding.currentSpeed.text = meterPerSecToMilesPerHour(currentSpeed).toString()

        if (currentSpeed > currentSpeedLimit && currentSpeedLimit > 0) {
            binding.speedInfoContainer.setBackgroundResource(R.color.Red)
        } else {
            binding.speedInfoContainer.setBackgroundResource(R.color.Green)
        }
    }

    private val instructListener: NewInstructionEventListener =
        object : NewInstructionEventListener() {
            override fun onNewInstructionEvent() {
                navigationManager.nextManeuver
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

}



