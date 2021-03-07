///*
// * Copyright (c) 2011-2020 HERE Europe B.V.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.fourofourfound.aims_delivery.delivery.onGoing.maps
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.Toast
//import com.fourofourfound.aimsdelivery.R
//import com.here.android.mpa.common.*
//import com.here.android.mpa.guidance.AudioPlayerDelegate
//import com.here.android.mpa.guidance.NavigationManager
//import com.here.android.mpa.guidance.NavigationManager.MapUpdateMode
//import com.here.android.mpa.guidance.NavigationManager.NavigationManagerEventListener
//import com.here.android.mpa.mapping.AndroidXMapFragment
//import com.here.android.mpa.mapping.Map
//import com.here.android.mpa.mapping.MapRoute
//import com.here.android.mpa.routing.*
//import kotlinx.android.synthetic.main.fragment_delivery_ongoing.view.*
//import java.io.File
//import java.lang.ref.WeakReference
//import java.util.*
//
///**
// * This class encapsulates the properties and functionality of the Map view.It also triggers a
// * turn-by-turn navigation from HERE Burnaby office to Langley BC.There is a sample voice skin
// * bundled within the SDK package to be used out-of-box, please refer to the Developer's guide for
// * the usage.
// */
//internal class MapFragmentView(private val m_activity: Activity,val  root: View) {
//    private var m_mapFragment: AndroidXMapFragment? = null
//    private lateinit var m_naviControlButton: Button
//    private var m_map: Map? = null
//    private var m_navigationManager: NavigationManager? = null
//    private var m_geoBoundingBox: GeoBoundingBox? = null
//    private var m_route: Route? = null
//    private val mapFragment: AndroidXMapFragment?
//        private get() = root.mapfragment
//
//    private fun initMapFragment() {
//        /* Locate the mapFragment UI element */
//        m_mapFragment = mapFragment
//
//        // This will use external storage to save map cache data, it is also possible to set
//        // private app's path
//        val path = File(m_activity.getExternalFilesDir(null), ".here-map-data")
//            .absolutePath
//        // This method will throw IllegalArgumentException if provided path is not writable
//        MapSettings.setDiskCacheRootPath(path)
//        if (m_mapFragment != null) {
//            /* Initialize the AndroidXMapFragment, results will be given via the called back. */
//            m_mapFragment!!.init { error ->
//                if (error == OnEngineInitListener.Error.NONE) {
//                    m_map = m_mapFragment!!.map
//                    m_map!!.setCenter(
//                        GeoCoordinate(49.259149, -123.008555),
//                        Map.Animation.NONE
//                    )
//                    //Put this call in Map.onTransformListener if the animation(Linear/Bow)
//                    //is used in setCenter()
//                    m_map!!.zoomLevel = 13.2
//                    /*
//                                 * Get the NavigationManager instance.It is responsible for providing voice
//                                 * and visual instructions while driving and walking
//                                 */m_navigationManager = NavigationManager.getInstance()
//                } else {
//                    AlertDialog.Builder(m_activity).setMessage(
//                        """
//                            Error : ${error.name}
//
//                            ${error.details}
//                            """.trimIndent()
//                    )
//                        .setTitle("ERROR")
//                        .setNegativeButton(android.R.string.cancel
//                        ) { dialog, which -> m_activity.finish() }
//                        .create().show()
//                }
//            }
//        }
//    }
//
//    private fun createRoute() {
//        /* Initialize a CoreRouter */
//        val coreRouter = CoreRouter()
//
//        /* Initialize a RoutePlan */
//        val routePlan = RoutePlan()
//
//        /*
//         * Initialize a RouteOption. HERE Mobile SDK allow users to define their own parameters for the
//         * route calculation,including transport modes,route types and route restrictions etc.Please
//         * refer to API doc for full list of APIs
//         */
//        val routeOptions = RouteOptions()
//        /* Other transport modes are also available e.g Pedestrian */routeOptions.transportMode =
//            RouteOptions.TransportMode.CAR
//        /* Disable highway in this route. */routeOptions.setHighwaysAllowed(false)
//        /* Calculate the shortest route available. */routeOptions.routeType =
//            RouteOptions.Type.SHORTEST
//        /* Calculate 1 route. */routeOptions.routeCount = 1
//        /* Finally set the route option */routePlan.routeOptions = routeOptions
//
//        /* Define waypoints for the route */
//        /* START: 4350 Still Creek Dr */
//        val startPoint = RouteWaypoint(GeoCoordinate(49.259149, -123.008555))
//        /* END: Langley BC */
//        val destination = RouteWaypoint(GeoCoordinate(49.073640, -122.559549))
//
//        /* Add both waypoints to the route plan */routePlan.addWaypoint(startPoint)
//        routePlan.addWaypoint(destination)
//
//        /* Trigger the route calculation,results will be called back via the listener */coreRouter
//            .calculateRoute(routePlan, object : Router.Listener<List<RouteResult>, RoutingError> {
//                override fun onProgress(i: Int) {
//                    /* The calculation progress can be retrieved in this callback. */
//                }
//
//                override fun onCalculateRouteFinished(
//                    routeResults: List<RouteResult>?,
//                    routingError: RoutingError
//                ) {
//                    /* Calculation is done.Let's handle the result */
//                    if (routingError == RoutingError.NONE) {
//                        if (routeResults!![0]!!.route != null) {
//                            m_route = routeResults[0].route
//                            /* Create a MapRoute so that it can be placed on the map */
//                            val mapRoute = MapRoute(
//                                routeResults[0].route
//                            )
//
//                            /* Show the maneuver number on top of the route */mapRoute.isManeuverNumberVisible =
//                                true
//
//                            /* Add the MapRoute to the map */m_map!!.addMapObject(mapRoute)
//
//                            /*
//                                 * We may also want to make sure the map view is orientated properly
//                                 * so the entire route can be easily seen.
//                                 */m_geoBoundingBox = routeResults[0].route
//                                .boundingBox
//                            m_map!!.zoomTo(
//                                m_geoBoundingBox!!, Map.Animation.NONE,
//                                Map.MOVE_PRESERVE_ORIENTATION
//                            )
//                            startNavigation()
//                        } else {
//                            Toast.makeText(
//                                m_activity,
//                                "Error:route results returned is not valid",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } else {
//                        Toast.makeText(
//                            m_activity, "Error:route calculation returned error code: "
//                                    + routingError,
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            })
//    }
//
//    private fun initNaviControlButton() {
//
//        m_naviControlButton.setOnClickListener(View.OnClickListener { /*
//                 * To start a turn-by-turn navigation, a concrete route object is required.We use
//                 * the same steps from Routing sample app to create a route from 4350 Still Creek Dr
//                 * to Langley BC without going on HWY.
//                 *
//                 * The route calculation requires local map data.Unless there is pre-downloaded map
//                 * data on device by utilizing MapLoader APIs,it's not recommended to trigger the
//                 * route calculation immediately after the MapEngine is initialized.The
//                 * INSUFFICIENT_MAP_DATA error code may be returned by CoreRouter in this case.
//                 *
//                 */
//            if (m_route == null) {
//                createRoute()
//            } else {
//                m_navigationManager!!.stop()
//                /*
//                         * Restore the map orientation to show entire route on screen
//                         */m_map!!.zoomTo(m_geoBoundingBox!!, Map.Animation.NONE, 0f)
//                m_naviControlButton.setText("START")
//                m_route = null
//            }
//        })
//    }
//
//
//
//    private fun receter() {
//        val m_voicePackagesButton = root.start
//        m_voicePackagesButton.setOnClickListener { recenterCode() }
//    }
//
//    private fun recenterCode() {
//        Log.i("AAAAAAA", "AAAAAAAAAAA")
//        m_navigationManager!!.mapUpdateMode = MapUpdateMode.ROADVIEW
//        Timer().schedule(
//            object : TimerTask() {
//                override fun run() {
//                    Log.i("AAAAAAA", "Changing")
//                    m_navigationManager!!.mapUpdateMode = MapUpdateMode.POSITION_ANIMATION
//                }
//            },
//            1000
//        )
//    }
//
//    /*
//     * Android 8.0 (API level 26) limits how frequently background apps can retrieve the user's
//     * current location. Apps can receive location updates only a few times each hour.
//     * See href="https://developer.android.com/about/versions/oreo/background-location-limits.html
//     * In order to retrieve location updates more frequently start a foreground service.
//     * See https://developer.android.com/guide/components/services.html#Foreground
//     */
//
//
//    private fun startNavigation() {
//        m_naviControlButton.setText("STOP")
//        /* Configure Navigation manager to launch navigation on current map */m_navigationManager!!.setMap(
//            m_map
//        )
//        m_mapFragment!!.positionIndicator!!.isVisible = true
//        /*
//         * Start the turn-by-turn navigation.Please note if the transport mode of the passed-in
//         * route is pedestrian, the NavigationManager automatically triggers the guidance which is
//         * suitable for walking. Simulation and tracking modes can also be launched at this moment
//         * by calling either simulate() or startTracking()
//         */
//
//        /* Choose navigation modes between real time navigation and simulation */
//        val alertDialogBuilder = AlertDialog.Builder(m_activity)
//        alertDialogBuilder.setTitle("Navigation")
//        alertDialogBuilder.setMessage("Choose Mode")
//        alertDialogBuilder.setNegativeButton("Navigation") { dialoginterface, i ->
//            m_navigationManager!!.startNavigation(m_route!!)
//            m_map!!.tilt = 60f
//        }
//        alertDialogBuilder.setPositiveButton("Simulation") { dialoginterface, i ->
//            m_navigationManager!!.simulate(m_route!!, 60) //Simualtion speed is set to 60 m/s
//            m_map!!.tilt = 60f
//        }
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//        /*
//         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
//         * the current location.If user gestures are expected during the navigation, it's
//         * recommended to set the map update mode to NONE first. Other supported update mode can be
//         * found in HERE Mobile SDK for Android (Premium) API doc
//         */m_navigationManager!!.mapUpdateMode = MapUpdateMode.ROADVIEW
//
//
//        /*
//         * Sets the measuring unit system that is used by voice guidance.
//         * Check VoicePackage.getCustomAttributes() to see whether selected package has needed
//         * unit system.
//         */m_navigationManager!!.distanceUnit = NavigationManager.UnitSystem.METRIC
//
//        /*
//         * NavigationManager contains a number of listeners which we can use to monitor the
//         * navigation status and getting relevant instructions.In this example, we will add 2
//         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
//         */addNavigationListeners()
//    }
//
//    private fun addNavigationListeners() {
//
//        /*
//         * Register a NavigationManagerEventListener to monitor the status change on
//         * NavigationManager
//         */
//        m_navigationManager!!.addNavigationManagerEventListener(
//            WeakReference(m_navigationManagerEventListener)
//        )
//
//        /* Register a PositionListener to monitor the position updates */m_navigationManager!!.addPositionListener(
//            WeakReference(m_positionListener)
//        )
//
//        /* Register a AudioPlayerDelegate to monitor TTS text */m_navigationManager!!.audioPlayer.setDelegate(
//            m_audioPlayerDelegate
//        )
//    }
//
//    private val m_positionListener: NavigationManager.PositionListener =
//        object : NavigationManager.PositionListener() {
//            override fun onPositionUpdated(geoPosition: GeoPosition) {
//                /* Current position information can be retrieved in this callback */
//            }
//        }
//    private val m_navigationManagerEventListener: NavigationManagerEventListener =
//        object : NavigationManagerEventListener() {
//            override fun onRunningStateChanged() {
//                recenterCode()
//                Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNavigationModeChanged() {
//                Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//            override fun onEnded(navigationMode: NavigationManager.NavigationMode) {
//                Toast.makeText(m_activity, "$navigationMode was ended", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//            override fun onMapUpdateModeChanged(mapUpdateMode: MapUpdateMode) {
//                Toast.makeText(
//                    m_activity, "Map update mode is changed to $mapUpdateMode",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onRouteUpdated(route: Route) {
//                Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCountryInfo(s: String, s1: String) {
//                Toast.makeText(
//                    m_activity, "Country info updated from $s to $s1",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    private val m_audioPlayerDelegate: AudioPlayerDelegate = object : AudioPlayerDelegate {
//        override fun playText(s: String): Boolean {
//            m_activity.runOnUiThread {
//                Toast.makeText(
//                    m_activity,
//                    "TTS output: $s",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            return false
//        }
//
//        override fun playFiles(strings: Array<String>): Boolean {
//            return false
//        }
//    }
//
//    fun onDestroy() {
//        /* Stop the navigation when app is destroyed */
//        if (m_navigationManager != null) {
//            m_navigationManager!!.stop()
//        }
//    }
//
//    init {
//        initMapFragment()
//        initNaviControlButton()
//        receter()
//    }
//}