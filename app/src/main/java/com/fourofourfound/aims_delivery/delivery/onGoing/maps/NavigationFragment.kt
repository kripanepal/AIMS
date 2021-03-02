package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.NavigationFragmentBinding
import com.here.android.mpa.common.GeoBoundingBox
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.routing.*
import java.util.*

class NavigationFragment : Fragment() {


    private lateinit var viewModel: NavigationViewModel
    lateinit var binding: NavigationFragmentBinding

    // map embedded in the map fragment
    lateinit var map: Map

    // map fragment embedded in this activity
    lateinit var mapFragment: AndroidXMapFragment
    lateinit var naviControlButton: Button
    lateinit var navigationManager: NavigationManager
    lateinit var geoBoundingBox: GeoBoundingBox
    var route: Route? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.navigation_fragment, container, false)
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)

        //assigning value to viewModel that is used by the layout
        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        binding.mapRecenterBtn.setOnClickListener { recenter() }
        initializeMap()
        return binding.root
    }


    private fun initializeMap() {

        mapFragment = childFragmentManager.findFragmentById(R.id.mapfragment) as AndroidXMapFragment

        mapFragment.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                // retrieve a reference of the map from the map fragment
                map = mapFragment.map!!
                // Set the map center to the Vancouver region (no animation)
                map.setCenter(
                    GeoCoordinate(49.196261, -123.004773, 0.0),
                    Map.Animation.BOW
                )
                // Set the zoom level to the average between min and max
                map.zoomLevel = (map.maxZoomLevel + map.minZoomLevel) / 2
                navigationManager = NavigationManager.getInstance()

                if (route !== null) {
                    navigationManager.resume()
                    recenter()
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


        val startPoint = RouteWaypoint(GeoCoordinate(49.259149, -123.008555))
        val destination = RouteWaypoint(GeoCoordinate(49.073640, -122.559549))

        routePlan.addWaypoint(startPoint)
        routePlan.addWaypoint(destination)

        coreRouter.calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onProgress(i: Int) {
                    /* The calculation progress can be retrieved in this callback. */
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
                                Map.MOVE_PRESERVE_ZOOM_LEVEL.toFloat()
                            )

                            startNavigation()


                        } else {
                            Toast.makeText(
                                context,
                                "Error:route results returned is not valid",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context, "Error:route calculation returned error code: "
                                    + routingError,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }

    private fun recenter() {
        navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.ROADVIEW

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    navigationManager.mapUpdateMode =
                        NavigationManager.MapUpdateMode.POSITION_ANIMATION
                }
            },
            2000
        )
    }

    private fun startNavigation() {

        navigationManager.setMap(map)
        mapFragment.positionIndicator!!.isVisible = true
        if (navigationManager.elapsedDistance == 0L) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Navigation")
            alertDialogBuilder.setMessage("Choose Mode")
            alertDialogBuilder.setNegativeButton("Navigation") { dialoginterface, i ->
                navigationManager.startNavigation(route!!)
                map.tilt = 60f
            }
            alertDialogBuilder.setPositiveButton("Simulation") { dialoginterface, i ->
                navigationManager.simulate(route!!, 60) //Simualtion speed is set to 60 m/s
                map.tilt = 60f
            }
            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }



        recenter()



        navigationManager.distanceUnit = NavigationManager.UnitSystem.METRIC

    }


}