package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
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
    var voiceId: Long = -1
    lateinit var map: Map
    lateinit var mapFragment: AndroidXMapFragment
    private var currentLatitude by Delegates.notNull<Double>()
    var currentLongitude by Delegates.notNull<Double>()
    lateinit var navigationManager: NavigationManager
    lateinit var geoBoundingBox: GeoBoundingBox
    var route: Route? = null
    private var fetchingDataInProgress = false

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.navigation_fragment, container, false)
        if (sharedViewModel.selectedTrip.value === null) {
            findNavController().navigate(R.id.ongoingDeliveryFragment)

            return binding.root
        }

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.mapRecenterBtn.setOnClickListener { recenter() }
        binding.destinationReached.setOnClickListener { destinationReached() }
        sharedViewModel.activeRoute?.apply { route = this }
        initializeMap()
        return binding.root
    }

    private fun initializeMap() {
        binding.progressBarContainer.visibility = View.VISIBLE
        mapFragment = childFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment
        mapFragment.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment.map!!
                navigationManager = NavigationManager.getInstance()
                if (route !== null) onRouteCalculated()
                else {
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
                    navigationManager.setMap(map)
                    mapFragment.positionIndicator?.isVisible = true
                    createRoute()
                }
            } else {
                Log.i("AAAA", error.toString())
            }
        }
    }


    private fun createRoute() {
        if (route == null) {
            val coreRouter = CoreRouter()
            val routePlan = RoutePlan()
            val routeOptions = RouteOptions()
            routeOptions.transportMode = RouteOptions.TransportMode.TRUCK
            //TODO need to change these parameters
            routeOptions.routeType = RouteOptions.Type.SHORTEST
            routeOptions.setTruckTunnelCategory(RouteOptions.TunnelCategory.E)
                .setTruckLength(25.25f)
                .setTruckHeight(2.6f).truckTrailersCount = 1
            routeOptions.routeCount = 1

            routePlan.routeOptions = routeOptions
            val startPoint = RouteWaypoint(GeoCoordinate(currentLatitude, currentLongitude))
            val destination = RouteWaypoint(GeoCoordinate(32.52568, -92.04272))
            routePlan.addWaypoint(startPoint)
            routePlan.addWaypoint(destination)
            coreRouter.calculateRoute(
                routePlan,
                object : Router.Listener<List<RouteResult>, RoutingError> {
                    override fun onProgress(i: Int) {}
                    override fun onCalculateRouteFinished(
                        routeResults: List<RouteResult>?,
                        routingError: RoutingError
                    ) {
                        if (routingError == RoutingError.NONE) {
                            if (routeResults!![0].route != null) {
                                route = routeResults[0].route
                                sharedViewModel.activeRoute = routeResults[0].route
                                onRouteCalculated()
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
    }

    private fun onRouteCalculated() {
        var givenRoute = route!!
        val mapRoute = MapRoute(givenRoute)
        mapRoute.isManeuverNumberVisible = true
        map.addMapObject(mapRoute)
        geoBoundingBox = givenRoute.boundingBox!!
        map.zoomTo(geoBoundingBox, Map.Animation.NONE, Map.MOVE_PRESERVE_TILT)
        map.mapScheme = Map.Scheme.TRUCKNAV_DAY
        startNavigation()
    }

    private fun recenter() {
        navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.ROADVIEW
        Handler(Looper.getMainLooper()).postDelayed({
            navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.POSITION_ANIMATION
        }, 2000)
    }

    private fun startNavigation() {
        changeNextManeuverTexts()
        changeViewsVisibility()
        navigationManager.setMap(map)
        mapFragment.positionIndicator?.isVisible = true
        if (navigationManager.runningState !== NavigationManager.NavigationState.RUNNING) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Navigation")
            alertDialogBuilder.setMessage("Choose Mode")
            alertDialogBuilder.setNegativeButton("Navigation") { _, _ ->
                navigationManager.startNavigation(route!!)
                map.tilt = 70f
            }
            alertDialogBuilder.setPositiveButton("Simulation") { _, _ ->
                navigationManager.simulate(route!!, 50)
                map.tilt = 70f
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialogBuilder.setCancelable(false)
            alertDialog.show()
        } else {
            mapFragment.onResume()
        }
        recenter()
        addListeners()
    }

    /**
     * Change views visibility
     *This method brings views to screen which are only required during navigation
     */
    private fun changeViewsVisibility() {
        binding.speedInfoContainer.visibility = View.VISIBLE
        binding.destinationReached.visibility = View.VISIBLE
        binding.mapRecenterBtn.visibility = View.VISIBLE
        binding.deliveryProgress.visibility = View.VISIBLE
        binding.nextInfoContainer.visibility = View.VISIBLE
        binding.progressBarContainer.visibility = View.GONE
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
        override fun onRerouteEnd(routeResult: RouteResult, error: RoutingError?) {
            super.onRerouteEnd(routeResult, error)
            map.removeAllMapObjects()
            map.addMapObject(MapRoute(routeResult.route))
            sharedViewModel.activeRoute = routeResult.route

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
                if (status != PrefetchStatus.PREFETCH_IN_PROGRESS) fetchingDataInProgress = false
            }
        }

    private var positionLister = object : PositioningManager.OnPositionChangedListener {
        override fun onPositionUpdated(
            locationMethod: PositioningManager.LocationMethod?,
            positionCoordinates: GeoPosition?,
            status: Boolean
        ) {
            if (PositioningManager.getInstance().roadElement == null && !fetchingDataInProgress) {
                val areaAround = positionCoordinates?.let {
                    GeoBoundingBox(
                        it.coordinate,
                        500F,
                        500F
                    )
                }
                if (areaAround != null) {
                    MapDataPrefetcher.getInstance().fetchMapData(areaAround)
                }
                fetchingDataInProgress = true
            }

            if (positionCoordinates != null && positionCoordinates.isValid && positionCoordinates is MatchedGeoPosition) {
                var completedDistance = navigationManager.elapsedDistance.toInt()
                val remainingDistance = navigationManager.destinationDistance.toDouble()

                binding.deliveryProgress.progress =
                    (100 - (remainingDistance / (completedDistance + remainingDistance)) * 100).toInt()
                //meter to miles
                var formatted =
                    String.format("%.2f", navigationManager.nextManeuverDistance * 0.000621371)
                binding.remainingDistance.text = formatted

                var currentSpeedLimit = 0.0
                val currentSpeed: Double = positionCoordinates.speed
                positionCoordinates.roadElement?.apply {
                    currentSpeedLimit = positionCoordinates.roadElement!!.speedLimit.toDouble()
                }
                updateSpeedTexts(currentSpeed, currentSpeedLimit)
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
        sharedViewModel.activeRoute = null
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
            Maneuver.Action.END
        }
    }

    private val instructListener: NewInstructionEventListener =
        object : NewInstructionEventListener() {
            override fun onNewInstructionEvent() {
                changeNextManeuverTexts()
                routeNameToImageMapper(navigationManager.nextManeuver?.icon)?.let {
                    binding.imageView2.setImageResource(
                        it
                    )
                }

            }
        }

    private fun changeNextManeuverTexts() {
        navigationManager.nextManeuver?.icon?.apply {
            viewModel.nextManeuverDirection.value =
                if (this == null) "Keep Straight" else routeNameToDirectionTextMapper(
                    navigationManager.nextManeuver?.icon
                )
        }

        navigationManager.afterNextManeuver?.roadName.apply {
            if (this != null) viewModel.nextManeuverRoadName.value = this
            else {
                viewModel.nextManeuverRoadName.value = "UNAVAILABLE"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MapEngine.getInstance().onResume()
        if (::navigationManager.isInitialized) {
            navigationManager.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        MapEngine.getInstance().onPause()
    }
}



