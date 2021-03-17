package com.fourofourfound.aims_delivery.homePage

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fourofourfound.aims_delivery.broadcastReceiver.NetworkChangedBroadCastReceiver
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aims_delivery.utils.BackgroundLocationPermissionUtil
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder
import com.fourofourfound.aims_delivery.utils.CustomWorkManager
import com.fourofourfound.aimsdelivery.R
import com.fourofourfound.aimsdelivery.databinding.FragmentHomePageBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home_page.*

/**
 * Home page
 * This fragment is responsible for displaying the list of trips that are available to the user
 * User can start the trip in the order as it is being displayed
 *
 * @constructor Create empty Home page
 */
class HomePage : Fragment() {

    /**
     * _binding
     * The binding object that is used by this fragment
     */
    private var _binding: FragmentHomePageBinding? = null

    /**
     * binding
     * The binding object that is used by this fragment which delegates to
     * _binding to prevent memory leaks
     */
    private val binding get() = _binding!!

    /**
     * Shared view model
     * ViewModel that contains shared information about the user and the
     * trip
     */
    private val sharedViewModel: SharedViewModel by activityViewModels()

    /**
     * View model
     *  the ViewModel that is used by the fragment to store the data
     */
    lateinit var viewModel: HomePageViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //create a binding object
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false
        )

        //navigate user to login screen if user is not logged in
        if (!sharedViewModel.userLoggedIn.value!!) {
            findNavController().navigate(HomePageDirections.actionHomePageToLoginFragment())
            return binding.root
        }

        //initialize viewModel and assign value to the viewModel in xml file
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        startTripOnClick()
        setUpRecyclerView()
        setUpSwipeToRefresh()
        registerBroadCastReceiver()

        setUpToolBar()

        viewModel.updating.observe(viewLifecycleOwner)
        {
            if (!it) {
                Log.i("CCCCCCCCCCCCCC", "HERE")
            }
        }

        return binding.root
    }

    /**
     * Set Up Tool Bar
     *This methods changes the visibility of toolbar and action bar to visible
     */
    private fun setUpToolBar() {
        requireActivity().apply {
            bottom_navigation.visibility = View.VISIBLE
            (this as AppCompatActivity?)?.supportActionBar!!.show()
        }
    }

    /**
     * Register broad cast receiver
     * This method registers for a broadcast receiver to listen for internet connection change.
     */
    private fun registerBroadCastReceiver() {
        if (!sharedViewModel.isLocationBroadcastReceiverInitialized) {
            val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            requireContext().registerReceiver(NetworkChangedBroadCastReceiver(), intentFilter)
            sharedViewModel.isLocationBroadcastReceiverInitialized = true
        }
    }


    /**
     * Set up swipe to refresh
     *Sets up swipe to refresh view which refreshes the trip list from the network
     */
    private fun setUpSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchTripFromNetwork()
            if (swipe_refresh.isRefreshing) swipe_refresh.isRefreshing = false

        }
    }

    /**
     * Set up recycler view
     *Sets up the recycler view to display the list of trips
     */
    private fun setUpRecyclerView() {
        //adapter for the recycler view
        val adapter = TripListAdapter(TripListListener { trip ->

            //set up the behaviour of button on the item being displayed
            if (trip.status == "COMPLETED") findNavController().navigate(
                HomePageDirections.actionHomePageToCompletedDeliveryFragment(trip)
            )
        })

        binding.tripList.adapter = adapter


        //observe for any changes on the trips and inform that to the user
        viewModel.tripList?.observe(viewLifecycleOwner) {
            //TODO new trip was added or modified. Need to send the notification to the user
            adapter.submitList(it)
        }
    }

    /**
     * Start trip on click
     * redirect the user to delivery page for a specific trip
     */
    private fun startTripOnClick() {
        binding.btnStartTrip.setOnClickListener {
            viewModel.tripList?.value?.get(0)?.let {
                val tripToStart = it
                if (tripToStart.status !== "COMPLETED")
                    showStartTripDialog(tripToStart)
            }
        }
    }

    /**
     * On destroy view
     *assign _binding to null to prevent memory leaks
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Show start trip dialog
     *Displays the Dialog to make sure that user wants to start the trip
     * On pressing yes, it also registers a worker which starts sending the
     * user location every 15 minutes[default]
     * @param tripToStart
     */
    private fun showStartTripDialog(tripToStart: Trip) {
        CustomDialogBuilder(
            requireContext(),
            "Start a trip?",
            null,
            "Start now",
            { markTripStart(tripToStart) },
            "No",
            null,
            true
        ).builder.show()
    }


    /**
     * This method checks for all required permissions to access the location in background.
     */
    override fun onStart() {
        super.onStart()
        BackgroundLocationPermissionUtil(requireContext()).checkPermissionsOnStart()
    }

    /**
     * Mark trip start
     * Starts a worker to start sending location updates and  navigates the user to
     * delivery page
     * @param tripToStart
     */
    private fun markTripStart(tripToStart: Trip) {
        sharedViewModel.selectedTrip.value = (tripToStart)
        CustomWorkManager(requireContext()).apply {
            //TODO need to call both methods
            sendLocationAndUpdateTrips()
            sendLocationOnetime()
        }

        //change the active tab to delivery tab
        requireActivity().bottom_navigation.selectedItemId = R.id.delivery_navigation
    }
}


