package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.here.android.mpa.common.GeoBoundingBox
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.common.PositioningManager
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.routing.*
import java.lang.ref.WeakReference
import kotlin.properties.Delegates


class NavigationFragment : Fragment() {


    private lateinit var viewModel: NavigationViewModel
    lateinit var binding: NavigationFragmentBinding
    lateinit var locationManager: LocationManager

    // map embedded in the map fragment
    lateinit var map: Map

    // map fragment embedded in this activity
    lateinit var mapFragment: AndroidXMapFragment
    var currentLatidude by Delegates.notNull<Double>()
    var currentLongitude by Delegates.notNull<Double>()


    lateinit var navigationManager: NavigationManager
    lateinit var geoBoundingBox: GeoBoundingBox
    var route: Route? = null

    lateinit var mPrefs: SharedPreferences


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
                // retrieve a reference of the map from the map fragment
                map = mapFragment.map!!

                PositioningManager.getInstance().lastKnownPosition.coordinate.apply {
                    currentLatidude = latitude
                    currentLongitude = longitude
                }
                map.setCenter(
                    GeoCoordinate(currentLatidude, currentLongitude, 0.0),
                    Map.Animation.NONE
                )

                // Set the zoom level to the average between min and max
                map.zoomLevel = (map.maxZoomLevel + map.minZoomLevel) / 2

                navigationManager = NavigationManager.getInstance()


                if (route !== null) {

                    navigationManager.resume()
                    binding.mapFragmentContainer.visibility = View.VISIBLE
                    map = mapFragment.map!!
                } else {
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


        val startPoint = RouteWaypoint(GeoCoordinate(currentLatidude, currentLongitude))
        val destination = RouteWaypoint(GeoCoordinate(32.52406, -92.09638))

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
                                Map.MOVE_PRESERVE_ZOOM_LEVEL.toFloat()
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
                            context, "Error:route calculation returned error code: "
                                    + routingError,
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
        mapFragment.positionIndicator!!.isVisible = true

        if (navigationManager.runningState !== NavigationManager.NavigationState.RUNNING) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Navigation")
            alertDialogBuilder.setMessage("Choose Mode")
            alertDialogBuilder.setNegativeButton("Navigation") { dialoginterface, i ->
                navigationManager.startNavigation(route!!)
                map.tilt = 70f
            }
            alertDialogBuilder.setPositiveButton("Simulation") { dialoginterface, i ->
                navigationManager.simulate(route!!, 100)
                map.tilt = 70f
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }


        recenter()
        binding.mapFragmentContainer.visibility = View.VISIBLE
        navigationManager.distanceUnit = NavigationManager.UnitSystem.METRIC
        navigationManager.addRerouteListener(WeakReference(rerouteListener))
        navigationManager.addNavigationManagerEventListener(WeakReference(routeCompleteListener))

    }

    var rerouteListener = object : NavigationManager.RerouteListener() {
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

    //Task to be done once destination is reached
    private fun destinationReached() {
        navigationManager.stop()
        lifecycleScope.launchWhenResumed {
            findNavController().navigateUp()
        }

    }


}