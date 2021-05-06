package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.domain.GeoCoordinates
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.animateViewVisibility
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentNavigationBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.here.android.mpa.common.*
import com.here.android.mpa.guidance.NavigationManager
import com.here.android.mpa.guidance.NavigationManager.NewInstructionEventListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapLabeledMarker
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.prefetcher.MapDataPrefetcher
import com.here.android.mpa.prefetcher.MapDataPrefetcher.Listener.PrefetchStatus
import com.here.android.mpa.routing.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.Delegates


/**
 * Navigation fragment
 * This fragment is responsible for navigating in map.
 * @constructor Create empty Navigation fragment
 */
class NavigationFragment : androidx.fragment.app.Fragment() {

    /**
     * View model
     * Viewmodel instance used to store the data of the fragment.
     */
    private lateinit var viewModel: NavigationViewModel

    /**
     * Binding
     * The binding object that is used by this fragment.
     */
    lateinit var binding: FragmentNavigationBinding

    /**
     * Map
     * The map shown in the fragment.
     */
    lateinit var map: Map

    /**
     * Map fragment
     * The fragment that hosts the map.
     */
    lateinit var mapFragment: AndroidXMapFragment

    /**
     * Current latitude
     * The current latitude of the user.
     */
    private var currentLatitude by Delegates.notNull<Double>()

    /**
     * Current longitude
     * The current longitude of the user.
     */
    var currentLongitude by Delegates.notNull<Double>()

    /**
     * Navigation manager
     * The navigation manager instance for controlling the navigation
     */
    lateinit var navigationManager: NavigationManager

    /**
     * Geo bounding box
     * The box that holds the start and end point on the map.
     */
    lateinit var geoBoundingBox: GeoBoundingBox

    /**
     * Route
     * The current route of the navigation manager.
     */
    var route: Route? = null

    /**
     * Fetching data in progress
     */
    private var fetchingDataInProgress = false

    /**
     * Source or site
     * The current destination.
     */
    private lateinit var sourceOrSite: SourceOrSite

    /**
     * Delivery status view model
     * The view model used to send status information to the dispatcher.
     */
    private val deliveryStatusViewModel: DeliveryStatusViewModel by activityViewModels()

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()


    /**
     * On create view
     * This method initializes the fragment.
     * @param inflater the inflater that is used to inflate the view
     * @param container the container that holds the fragment
     * @param savedInstanceState called when fragment is starting
     * @return the view that is inflated
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navigation, container, false)
        if (sharedViewModel.selectedTrip.value === null || sharedViewModel.selectedSourceOrSite.value === null) {
        } else sourceOrSite = sharedViewModel.selectedSourceOrSite.value!!
        viewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.destinationReachedBtn.setOnClickListener { destinationReached() }
        sharedViewModel.activeRoute?.apply { route = this }
        return binding.root
    }

    /**
     * Set up draggable view
     * This method sets up the draggable view at the bottom of the map
     */
    private fun setUpDraggableView() {
        val bottomSheetCallBack = object : BottomSheetBehavior.BottomSheetCallback() {

            /**
             * Change margin to draggable view
             * This method changes the position of the view passed
             * @param view the view to be moved
             * @param expanded the current state of the bottom sheet
             */
            fun changeMarginToDraggableView(view: View, expanded: Float) {
                val draggableView = binding.draggableView
                var layoutParams = (view.layoutParams as ViewGroup.MarginLayoutParams)
                layoutParams.bottomMargin =
                    (((draggableView.height * expanded) / 1.4) + BottomSheetBehavior.from(
                        draggableView
                    ).peekHeight).toInt()
                view.layoutParams = layoutParams
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            /**
             * On slide
             * Called when the bottom sheet is being dragged.
             * @param bottomSheet The bottom sheet view.
             * @param slideOffset The new offset of this bottom sheet within [-1,1] range. Offset increases
             *     as this bottom sheet is moving upward. From 0 to 1 the sheet is between collapsed and
             *     expanded states and from -1 to 0 it is between hidden and collapsed states.
             */
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                changeMarginToDraggableView(binding.mapRecenter, slideOffset)
                changeMarginToDraggableView(binding.speedInfoContainer, slideOffset)
            }
        }
        BottomSheetBehavior.from(binding.draggableView).addBottomSheetCallback(bottomSheetCallBack)
        binding.destinationInfo.apply {
            sourceOrSiteName.text = sourceOrSite.location.destinationName
            address.text = sourceOrSite.location.address1
            productDesc.text = sourceOrSite.productInfo.productDesc
            trailerText.text = sourceOrSite.trailerInfo.trailerDesc
            truckText.text = sourceOrSite.truckInfo.truckDesc
            val qty = "${sourceOrSite.productInfo.requestedQty} ${sourceOrSite.productInfo.uom}"
            productQty.text = qty
        }

    }

    /**
     * On view created
     * This method is called when the view is created
     * @param view the view that is created
     * @param savedInstanceState called when fragment is started
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeMap()
        if (sharedViewModel.selectedSourceOrSite.value != null) {
            setUpDraggableView()
            binding.mapRecenter.setOnClickListener { recenter() }
        } else binding.progressBarContainer.visibility = View.GONE
    }

    /**
     * Initialize map
     * This method initialized the map.
     */
    private fun initializeMap() {
        // This will use external storage to save map cache data
        val path: String = File(requireActivity().getExternalFilesDir(null), ".here-map-data")
            .absolutePath
        MapSettings.setDiskCacheRootPath(path)
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
                    navigationManager.setMap(map)
                    mapFragment.positionIndicator?.isVisible = true
                    recenter()
                    if (sharedViewModel.selectedSourceOrSite.value != null) checkAndCreateRoute()
                }
            }
        }
    }

    /**
     * Check and create route
     * This method checks if there is an existing route or creates a new one if there is non.
     */
    private fun checkAndCreateRoute() {
        if (route == null) {
            val coreRouter = CoreRouter()
            val routePlan = RoutePlan()
            val routeOptions = RouteOptions()
            coreRouter.connectivity = CoreRouter.Connectivity.DEFAULT
            if (sharedViewModel.internetConnection.value == false) {
                context?.let {
                    CustomDialogBuilder(
                        it,
                        "No Internet Connection",
                        "Route results may not be accurate without internet connection",
                        "Continue",
                        {
                            routeOptions.transportMode = RouteOptions.TransportMode.UNDEFINED
                            createRoute(routeOptions, routePlan, coreRouter)
                        },
                        "Cancel Navigation",
                        { findNavController().navigateUp() },
                        false
                    ).builder.show()
                }
            } else {
                routeOptions.transportMode = RouteOptions.TransportMode.TRUCK
                routeOptions.setTruckTunnelCategory(RouteOptions.TunnelCategory.E)
                    .setTruckLength(25.25f)
                    .setTruckHeight(2.6f).truckTrailersCount = 1
                createRoute(routeOptions, routePlan, coreRouter)
            }
        }
    }

    /**
     * Create route
     * This method creates route from one point to another.
     * @param routeOptions the options used for the route
     * @param routePlan route plan object to hold the geo coordinates
     * @param coreRouter the router object used to create a route
     */
    private fun createRoute(
        routeOptions: RouteOptions,
        routePlan: RoutePlan,
        coreRouter: CoreRouter
    ) {
        routeOptions.routeType = RouteOptions.Type.SHORTEST
        routeOptions.routeCount = 1
        routePlan.routeOptions = routeOptions
        val startPoint = RouteWaypoint(GeoCoordinate(currentLatitude, currentLongitude))
        val destination =
            RouteWaypoint(
                GeoCoordinate(
                    sourceOrSite.location.latitude,
                    sourceOrSite.location.longitude
                )
            )
        routePlan.addWaypoint(startPoint)
        routePlan.addWaypoint(destination)
        coreRouter.calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onProgress(i: Int) {}

                /**
                 * On calculate route finished
                 * Called when route calculation is finished.
                 * @param routeResults the result of the calculated route
                 * @param routingError the routing error if there is any
                 */
                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>,
                    routingError: RoutingError
                ) {
                    if (routingError == RoutingError.NONE) {
                        route = routeResults?.get(0)?.route
                        onRouteCalculated()
                    } else {
                        Toast.makeText(context, "Error: $routingError", Toast.LENGTH_LONG).show()
                        showErrorDialog()
                    }
                }
            })
    }

    /**
     * On route calculated
     * This method is called when the new route is calculated.
     */
    private fun onRouteCalculated() {
        map.removeAllMapObjects()
        addDestinationMarker()
        val givenRoute = route!!
        val mapRoute = MapRoute(givenRoute)
        mapRoute.isManeuverNumberVisible = true
        map.addMapObject(mapRoute)
        geoBoundingBox = givenRoute.boundingBox!!
        map.zoomTo(route!!.boundingBox!!, Map.Animation.BOW, 20.0f, 0f)
        map.mapScheme = Map.Scheme.TRUCKNAV_DAY
        startNavigation()
        binding.progressBarContainer.visibility = View.GONE
    }

    /**
     * Add destination marker
     * This method adds the destination marker in the map.
     */
    private fun addDestinationMarker() {
        val makerImage = Image()
        try {
            var icon =
                if (sourceOrSite.wayPointTypeDescription == "Source") R.drawable.ic_source else R.drawable.ic_site
            context?.let {
                ResourcesCompat.getDrawable(it.resources, icon, null)
                    ?.let { bitmap -> makerImage.setBitmap(bitmap.toBitmap(125, 125)) }
            }
            val myMapMarker = MapLabeledMarker(
                GeoCoordinate(
                    sourceOrSite.location.latitude,
                    sourceOrSite.location.longitude
                ), makerImage
            )
            myMapMarker.setLabelText(map.mapDisplayLanguage, sourceOrSite.location.destinationName)
            myMapMarker.fontScalingFactor = 4F
            myMapMarker.fontScalingFactor
            map.addMapObject(myMapMarker)

        } catch (e: Exception) {
        }
    }

    /**
     * Recenter
     * This method re centers the map.
     */
    private fun recenter() {
        PositioningManager.getInstance().lastKnownPosition.coordinate.apply {
            map.setCenter(GeoCoordinate(latitude, longitude), Map.Animation.BOW, 18.0, 0f, 0f)
        }
        navigationManager.mapUpdateMode = NavigationManager.MapUpdateMode.ROADVIEW

    }

    /**
     * Start navigation
     * This method starts the navigation.
     */
    private fun startNavigation() {
        changeNextManeuverTexts()
        navigationManager.setMap(map)
        if (navigationManager.runningState !== NavigationManager.NavigationState.RUNNING) {
            showBottomSheetWithAnimation()
            binding.startBtn.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Navigation")
                alertDialogBuilder.setMessage("Choose Mode")
                alertDialogBuilder.setNegativeButton("Navigation") { _, _ ->
                    navigationManager.startNavigation(route!!)
                    sharedViewModel.activeRoute = route
                    bottomSheetNavigationStarted()
                    changeViewsVisibility()
                    recenter()
                    addListeners()
                }
                alertDialogBuilder.setPositiveButton("Simulation") { _, _ ->
                    navigationManager.simulate(route!!, 100)
                    sharedViewModel.activeRoute = route
                    bottomSheetNavigationStarted()
                    changeViewsVisibility()
                    recenter()
                    addListeners()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialogBuilder.setCancelable(false)
                alertDialog.show()
            }
        } else {
            changeViewsVisibility()
            bottomSheetNavigationStarted()
            mapFragment.onResume()
            mapFragment.map?.positionIndicator?.isVisible = true
        }
    }

    /**
     * Bottom sheet navigation started
     * This method turn on the visibility of views that are used for navigation.
     */
    private fun bottomSheetNavigationStarted() {
        binding.startReachedContainer.visibility = View.VISIBLE
        binding.startBtn.visibility = View.GONE
        binding.destinationInfo.root.visibility = View.VISIBLE
        BottomSheetBehavior.from(binding.draggableView).state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /**
     * Show bottom sheet with animation
     * This method shows bottom sheet in the map fragment with the animation.
     */
    private fun showBottomSheetWithAnimation() {
        animateViewVisibility(binding.startReachedContainer, binding.startReachedContainer, true)
        animateViewVisibility(binding.root, binding.destinationInfo.root, true)
        Handler(Looper.getMainLooper()).postDelayed({
            BottomSheetBehavior.from(binding.draggableView).state =
                BottomSheetBehavior.STATE_EXPANDED
        }, 1000)
    }

    /**
     * Change views visibility
     * This method brings views to screen which are only required during navigation
     */
    private fun changeViewsVisibility() {
        binding.deliveryProgress.visibility = View.VISIBLE
        animateViewVisibility(binding.root, binding.nextInfoContainer, true)
    }

    /**
     * Add listeners
     * This methods adds the navigation listeners.
     */
    private fun addListeners() {
        deliveryStatusViewModel.locationRepository.addListener()
        mapFragment.mapGesture!!.addOnGestureListener(MyOnGestureListener(), 1, false)
        navigationManager.distanceUnit = NavigationManager.UnitSystem.IMPERIAL_US
        navigationManager.addRerouteListener(WeakReference(rerouteListener))
        navigationManager.addNavigationManagerEventListener(WeakReference(routeCompleteListener))
        MapDataPrefetcher.getInstance().addListener(prefetchListener)
        PositioningManager.getInstance().addListener(WeakReference(positionLister))
        navigationManager.addNewInstructionEventListener(WeakReference(instructListener))
        setUpVoiceNavigation()
    }

    /**
     * Remove listeners
     * This method removes the navigation listeners.
     */
    private fun removeListeners() {
        navigationManager.apply {
            deliveryStatusViewModel.locationRepository.removeListener()
            navigationManager.removeRerouteListener(rerouteListener)
            navigationManager.removeNavigationManagerEventListener(routeCompleteListener)
            MapDataPrefetcher.getInstance().removeListener(prefetchListener)
            PositioningManager.getInstance().removeListener(positionLister)
            navigationManager.stop()
        }
    }

    /**
     * Reroute listener
     * Reroute listener used when rerouting is done.
     */
    private var rerouteListener = object : NavigationManager.RerouteListener() {

        /**
         * On reroute end
         * Called when routing ends
         * @param routeResult new route results
         * @param error error if any
         */
        override fun onRerouteEnd(routeResult: RouteResult, error: RoutingError?) {
            super.onRerouteEnd(routeResult, error)
            map.removeAllMapObjects()
            addDestinationMarker()
            map.addMapObject(MapRoute(routeResult.route))
            sharedViewModel.activeRoute = routeResult.route
        }
    }

    /**
     * Route complete listener
     * Called when routing completes.
     */
    private var routeCompleteListener =
        object : NavigationManager.NavigationManagerEventListener() {

            /**
             * On destination reached
             * Called when destination is reached.
             */
            override fun onDestinationReached() {
                destinationReached()
                super.onDestinationReached()
            }
        }

    /**
     * Prefetch listener
     * Called when map data is being fetched.
     */
    private var prefetchListener: MapDataPrefetcher.Adapter =
        object : MapDataPrefetcher.Adapter() {
            override fun onStatus(requestId: Int, status: PrefetchStatus) {
                if (status != PrefetchStatus.PREFETCH_IN_PROGRESS) fetchingDataInProgress = false
            }
        }

    /**
     * Position lister
     * Called when position of the user is updated.
     */
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
                mapFragment.map?.positionIndicator?.isVisible = true
                val completedDistance = navigationManager.elapsedDistance.toInt()
                val remainingDistance = navigationManager.destinationDistance.toDouble()

                binding.deliveryProgress.progress =
                    (100 - (remainingDistance / (completedDistance + remainingDistance)) * 100).toInt()
                //meter to miles
                val formatted = if (navigationManager.nextManeuverDistance < Long.MAX_VALUE)
                    String.format(
                        "%.2f",
                        navigationManager.nextManeuverDistance * 0.000621371
                    ) else "calculating"
                binding.remainingDistance.text = formatted

                var currentSpeedLimit = 0.0
                val currentSpeed: Double = positionCoordinates.speed
                positionCoordinates.roadElement?.apply {
                    currentSpeedLimit = positionCoordinates.roadElement!!.speedLimit.toDouble()
                }
                updateSpeedTexts(currentSpeed, currentSpeedLimit)
                updateTimeToArrivalText(positionCoordinates)
            }
        }

        override fun onPositionFixChanged(
            p0: PositioningManager.LocationMethod?,
            p1: PositioningManager.LocationStatus?
        ) {
        }
    }

    /**
     * Update time to arrival text
     * This method updates the time remaining till the arrival to the destination.
     * @param positionCoordinates the geo coordinate of the user
     */
    private fun updateTimeToArrivalText(positionCoordinates: MatchedGeoPosition) {
        val millis =
            (navigationManager.getEta(true, Route.TrafficPenaltyMode.OPTIMAL).time).minus(
                Calendar.getInstance().time.time
            )
        val hours = (millis / (1000 * 60 * 60))
        val mins = ((millis / (1000 * 60)) % 60)
        var remainingTime = if (hours > 0) "$hours hr $mins min" else "$mins min"
        if (mins >= 0)
            binding.remainingTime.text = remainingTime
        else binding.remainingTime.text = "Arrived"
        deliveryStatusViewModel.locationRepository.coordinates.value = GeoCoordinates(
            positionCoordinates.coordinate.latitude,
            positionCoordinates.coordinate.longitude
        )
    }


    /**
     * Destination reached
     * This method performs task to be done once destination is reached.
     */
    private fun destinationReached() {
        lifecycleScope.launchWhenResumed {
            context?.let {
                CustomDialogBuilder(
                    it,
                    "Show Details",
                    "Go back to the details page",
                    "OK",
                    {
                        sharedViewModel.activeRoute = null
                        removeListeners()
                        findNavController().navigateUp()
                    },
                    "Cancel",
                    null,
                    false
                ).builder.show()
            }
        }
    }

    /**
     * Meter per sec to miles per hour
     * This method converts the unit of the speed.
     * @param speed the speed in meter per sec
     * @return speed in miles per hour
     */
    private fun meterPerSecToMilesPerHour(speed: Double): Int {
        return (speed * 2.23694).toInt()
    }

    /**
     * Update speed texts
     * This method updates the speed text.
     * @param currentSpeed  the current speed of the truck
     * @param currentSpeedLimit the speed limit for the road
     */
    private fun updateSpeedTexts(currentSpeed: Double, currentSpeedLimit: Double) {
        if (currentSpeedLimit > 0) {
            binding.speedInfoContainer.visibility = View.VISIBLE
            val currentSpeedLimitText =
                (meterPerSecToMilesPerHour(currentSpeedLimit) + 1).toString()
            binding.currentSpeedLimit.text = currentSpeedLimitText
            binding.currentSpeed.text = (meterPerSecToMilesPerHour(currentSpeed) + 1).toString()
            if (currentSpeed > currentSpeedLimit && currentSpeedLimit > 0) {
                binding.speedInfoContainer.setBackgroundResource(R.color.Red)
            } else {
                binding.speedInfoContainer.setBackgroundResource(R.color.white)
            }
        } else {
            binding.speedInfoContainer.visibility = View.GONE
        }
    }

    /**
     * Instruct listener
     * This method listens to any event change.
     */
    private val instructListener: NewInstructionEventListener =
        object : NewInstructionEventListener() {

            /**
             * On new instruction event
             * Called when there is change in the maneuver text and shows the image accordingly.
             */
            override fun onNewInstructionEvent() {
                changeNextManeuverTexts()
                viewModel.nextManeuverArrow.value =
                    routeNameToImageMapper(navigationManager.nextManeuver?.icon)
            }
        }

    /**
     * Change next maneuver texts
     * This method shows which direction to turn next.
     */
    private fun changeNextManeuverTexts() {
        navigationManager.nextManeuver?.icon?.apply {
            viewModel.nextManeuverDirection.value =
                if (this == null) "Keep Straight" else routeNameToDirectionTextMapper(
                    navigationManager.nextManeuver?.icon
                )
        }
        navigationManager.afterNextManeuver?.roadName?.apply {
            viewModel.nextManeuverRoadName.value = this.substringBefore('/')
        }
    }

    /**
     * On resume
     * Called when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        MapEngine.getInstance().onResume()
        if (::navigationManager.isInitialized) {
            navigationManager.resume()
        }
        requireActivity().bottom_navigation.visibility =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                View.GONE
            else View.VISIBLE
    }

    /**
     * On pause
     * Called when the fragment is paused.
     */
    override fun onPause() {
        super.onPause()
        MapEngine.getInstance().onPause()
    }

    /**
     * Show error dialog
     * This method shows the error dialog box.
     */
    private fun showErrorDialog() {
        context?.let {
            CustomDialogBuilder(
                it,
                "Something Went Wrong",
                "Please check you internet connection. Maps may not be available offline. \n\nYou can download maps for offline use in settings tab",
                "Retry",
                { initializeMap() },
                "Go Back",
                { findNavController().navigateUp() },
                false
            ).builder.show()
        }
    }
}